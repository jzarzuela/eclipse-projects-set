/**
 * 
 */
package com.jzb.fdf.srvc;

import com.jzb.fdf.srvc.impl.ModelSrvcImpl;

/**
 * @author jzarzuela
 * 
 */
public interface IModelSrvc {

    public final IModelSrvc inst = new ModelSrvcImpl();

    public void done();

    public void init(boolean deleteDB);

    public void processDuplicatedFiles() throws Exception;

}
