#!/usr/bin/bash

cd $PROJECT
git stash push -m $(`date`)
git pull --rebase




