#!/usr/bin/env bash

set -e -x

#ab -r -l -c 250 -n 90000  -v 0 -T 'application/json' "https://account-homochrome-melammed.pcf1.fe.gopivotal.com/foo" &
ab -r -l -c 25 -n 900  -p account.json -v 0 -T 'application/json' "https://account-service.app.13.90.33.125.cf.pcfazure.com/accounts"

