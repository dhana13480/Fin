#!/bin/bash -xev

mkdir ${WORKSPACE}/release/
cd ${WORKSPACE}/checkout

pwd & ls -lrt deliverystream/

cp -rf deliverystream/* ${WORKSPACE}/release/
