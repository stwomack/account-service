#!/usr/bin/env bash

set -e -x

for i in `seq 100 10000`; do
    curl -i -k -X DELETE https://accountz.pcf1.fe.gopivotal.com/accounts/$i
done
