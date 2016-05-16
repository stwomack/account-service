#!/usr/bin/env bash

set -e -x

ab -r -l -c 50 -n 20000  -p account.json -v 0 -T 'application/json' "https://accountz.pcf1.fe.gopivotal.com/accounts"