/**
 * 
 */
package com.jzb.fdf.srvc.impl;

/**
 * @author jzarzuela
 * 
 */
interface IItemProcessor<T> {

    public void processItem(T item) throws Exception;
}
