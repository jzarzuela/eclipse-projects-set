@echo off
setlocal
call setEnv.cmd
start javaw -cp "%cp%" com.jzb.vt.MainCard %THIS_WKSP%\resources\datos.xls
