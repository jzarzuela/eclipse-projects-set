/**
 * 
 */
package com.jzb.basic.cdi;

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.util.FilterBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @author jzarzuela
 * 
 */
public class CPScanInjectorModule extends AbstractModule {

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        _scannForProviders();
    }

    // ----------------------------------------------------------------------------------------------------
    private void _scannForProviders() {

        Reflections reflections = new Reflections();
        Set<Scanner> scanners = reflections.getConfiguration().getScanners();
        for (Scanner scanner : scanners) {
            scanner.filterResultsBy(new FilterBuilder().exclude("java.*"));
            scanner.filterResultsBy(new FilterBuilder().exclude("javax.*"));
        }

        Set<Class<?>> providers = reflections.getTypesAnnotatedWith(Provider.class);
        for (Class<?> provider : providers) {
            _associateProvider(provider);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _associateProvider(Class provider) {

        Provider ann = (Provider) provider.getAnnotation(Provider.class);
        if (ann.named().equals("")) {
            bind(ann.service()).to(provider);
        } else {
            bind(ann.service()).annotatedWith(Names.named(ann.named())).to(provider);
        }
    }

}
