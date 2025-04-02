@echo off
echo Running automated Git push...
cd /d "d:\develop\projects\blog"
git add .
git commit -m "Automated commit at %TIME%"
git push
echo Git push completed.
