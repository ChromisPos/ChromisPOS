@echo off

set DIRNAME=%~dp0
set CP="%DIRNAME%defaultresets.jar"
start /B javaw -cp %CP% uk.chromis.defaultresets.DefaultResets
