/**
 * 
 */
package com.jzb.fdf.prcs;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jzb.fdf.srvc.IFileSrvc;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
class FileProcessor {

    // ----------------------------------------------------------------------------------------------------
    private static final char[]               _HEX_CHARS                     = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };
    private static final int                  CHUNK_SIZE                     = 1024;                                                                               // 1024;
    private static final int                  MAX_FILE_PROCESSORS            = 4 * 4;                                                                              // 4 * 3 * 4;
    private static final int                  NUM_CHUNKS                     = 3;
    private static MyAtomicInteger            s_completedFileProcessorsCount = new MyAtomicInteger(0);
    private static MyAtomicInteger            s_executingFileProcessorsCount = new MyAtomicInteger(0);
    private static CountDownLatch             s_executionCompletedLatch;
    private static ExecutorService            s_fileExecutor;
    private static ThreadLocal<FileProcessor> s_processorCache               = new ThreadLocal();
    private ByteBuffer                        m_byteBuffer;
    private MessageDigest                     m_md5;
    private int                               m_totalReadLength;

    // ----------------------------------------------------------------------------------------------------
    private FileProcessor() throws RuntimeException {

        try {
            m_md5 = MessageDigest.getInstance("MD5");
            m_byteBuffer = ByteBuffer.allocateDirect(NUM_CHUNKS * CHUNK_SIZE);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating instance", ex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public static void _init() {

        s_executionCompletedLatch = new CountDownLatch(1);
        s_executingFileProcessorsCount.set(0);
        s_completedFileProcessorsCount.set(0);
        s_fileExecutor = Executors.newFixedThreadPool(MAX_FILE_PROCESSORS);
    }

    // ----------------------------------------------------------------------------------------------------
    public static void awaitTermination() throws InterruptedException {

        Tracer._info("File Processor - Awaiting termination");
        if (s_executingFileProcessorsCount.get() > 0) {
            s_executionCompletedLatch.await();
        }
        s_fileExecutor.shutdown();
        s_fileExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        Tracer._info("File Processor - ALL FILE PROCESSORS ARE DONE");
    }

    // ----------------------------------------------------------------------------------------------------
    public static void cancel() {

        // Manda cancelar todo y libera las cuentas de las tareas pendientes de ejecucion
        Tracer._warn("File Processor - Canceling execution");
        s_fileExecutor.shutdownNow();
        s_executionCompletedLatch.countDown();
        s_executingFileProcessorsCount.set(0);
    }

    // ----------------------------------------------------------------------------------------------------
    public static int completedProcessorsCount() {
        return s_completedFileProcessorsCount.get();
    }

    // ----------------------------------------------------------------------------------------------------
    public static int pendingProcessorsCount() {
        return s_executingFileProcessorsCount.get();
    }

    // ----------------------------------------------------------------------------------------------------
    public static void spawnNewProcessor(final File folder, final String fname) {

        // Tracer._debug("File Processor - Spawning new instance for '" + file + "'");
        _executionStartAccounting();

        try {
            s_fileExecutor.execute(new Runnable() {

                @Override
                @SuppressWarnings("synthetic-access")
                public void run() {

                    try {
                        // Solicita una instancia y la pone en ejecucion
                        // Tracer._debug("File Processor - Waiting for free instance in pool for '" + file + "'");
                        FileProcessor processor = s_processorCache.get();
                        if (processor == null) {
                            processor = new FileProcessor();
                            s_processorCache.set(processor);
                        }

                        // Tracer._debug("File Processor - Starting processing for '" + file + "'");
                        processor._processFile(folder, fname);
                        // Tracer._debug("+ File Processor - Processing has finished for '" + file + "'");

                    } catch (ClosedByInterruptException cbie) {
                        // Posiblemente cerrado por cancelacion
                        if (!s_fileExecutor.isShutdown()) {
                            Tracer._error("? File Processor - Error while processing '" + fname + "' in '" + folder + "'", cbie);
                        }
                    } catch (Throwable th) {
                        Tracer._error("? File Processor - Error while processing '" + fname + "' in '" + folder + "'", th);
                    } finally {
                        // Avisa de que ha terminado
                        _executionEndAccounting();
                    }
                }
            });
        } catch (Throwable th) {
            // La ejecucion ha sido cancelada... A todos los efectos esta finalizada
            _executionEndAccounting();
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private static void _executionEndAccounting() {
        s_completedFileProcessorsCount.incrementAndGet();
        if (s_executingFileProcessorsCount.decrementUntilZero() <= 0) {
            s_executionCompletedLatch.countDown();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private static void _executionStartAccounting() {
        s_executingFileProcessorsCount.incrementAndGet();
    }

    // ----------------------------------------------------------------------------------------------------
    private String _calcFileHashing(File file) throws Exception {

        // Reinicia el calculo de hashing
        m_md5.reset();

        // Guarda como parte de la informacion la longitud del fichero
        // Salvo que sea cero (vacio) en ese caso mete el nombre
        // (Mismo HASHING si nombre es igual y esta vacio)
        if (file.length() > 0) {
            m_md5.update(_longToByteArray(file.length()));
        } else {
            m_md5.update(file.getName().getBytes("UTF-8"));
        }

        // Actualiza el calculo con los datos leidos
        m_byteBuffer.rewind();
        m_byteBuffer.limit(m_totalReadLength);
        m_md5.update(m_byteBuffer);

        // Realiza el calculo y lo retorna
        String hash = _toHex(m_md5.digest());
        return hash;
    }

    // ----------------------------------------------------------------------------------------------------
    private byte[] _longToByteArray(long l) {

        // Guarda como parte de la informacion la longitud del fichero
        byte buffer[] = new byte[8];
        for (int n = 0; n < 8; n++) {
            buffer[n] = (byte) (l & 0x0FF);
            l = l >> 8;
        }
        return buffer;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processFile(final File folder, final String fname) throws Exception {

        // Consigue el fichero que tiene que procesar
        File file = new File(folder, fname);

        // Lee la informacion
        _readFileData(file);

        // Calcula el HASHING del fichero
        String hashing = _calcFileHashing(file);

        // Crea el MFile a partir de los datos
        IFileSrvc.inst.createInstance(folder, file.getName(), file.length(), file.lastModified(), hashing);

    }

    // ----------------------------------------------------------------------------------------------------
    private void _readFileData(File file) throws IOException {

        // Reinicia el buffer de lectura
        m_byteBuffer.rewind();
        m_byteBuffer.limit(m_byteBuffer.capacity());
        m_totalReadLength = 0;

        // Asi como el numero de lecturas atendiendo al tama�o
        int totalReadingsCount;
        long skipPos;
        if (file.length() > NUM_CHUNKS * CHUNK_SIZE) {
            totalReadingsCount = NUM_CHUNKS;
            skipPos = (file.length() - CHUNK_SIZE) / (NUM_CHUNKS - 1);
        } else {
            totalReadingsCount = 1;
            skipPos = 0;
        }

        // Crea el canal asincrono para el fichero a procesar
        try (FileChannel fChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {

            // Lee la informacion
            long currentPos = 0;
            for (int n = 0; n < totalReadingsCount; n++) {

                // Va incrementando el limite de lectura del buffer en cada iteracion para albergar el siguiente "chunk"
                if (totalReadingsCount > 1) {
                    m_byteBuffer.limit((n + 1) * CHUNK_SIZE);
                } else {
                    m_byteBuffer.limit((int) file.length());
                }

                // Actualiza la posicion de lectura
                currentPos = skipPos * n;
                int len = fChannel.read(m_byteBuffer, currentPos);
                if (len < 0) {
                    throw new IOException("No data was read from file channel");
                } else {
                    m_totalReadLength += len;
                }
            }
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private String _toHex(byte hash[]) {

        StringBuffer buf = new StringBuffer(hash.length * 2);

        for (int idx = 0; idx < hash.length; idx++)
            buf.append(_HEX_CHARS[(hash[idx] >> 4) & 0x0f]).append(_HEX_CHARS[hash[idx] & 0x0f]);

        return buf.toString();
    }
}
