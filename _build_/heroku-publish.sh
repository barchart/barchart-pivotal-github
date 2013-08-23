#!/bin/bash

#
# https://devcenter.heroku.com/articles/java
#

# remote origin
NAME="heroku"
# remote git url
SITE="git@heroku.com:barchart-pivotal-github.git"

pwd

git remote | grep $NAME || git remote add $NAME $SITE

git push $NAME master
