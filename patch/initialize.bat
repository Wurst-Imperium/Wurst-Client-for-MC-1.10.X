@echo off
rem # Script for initializing the Minecraft repository.
rem # Place the decompiled Minecraft source code, as generated by the MCP, in
rem # the "mc" folder before running this script!
rem # The repository that this script creates is required for the apply-patch
rem # and update-patch scripts. They will not work without it!

pause
cd ..\mc
git init
git add .
git commit -a -m "Initial commit"
git checkout -b modded
git apply --ignore-space-change --ignore-whitespace ..\patch\minecraft.patch
git commit -a -m "Add Wurst changes"
rem pause
