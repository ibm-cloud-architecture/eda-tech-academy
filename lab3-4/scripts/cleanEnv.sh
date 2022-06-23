#!/bin/bash

if [ "$1" == "poe" ]
then
    declare -a ns=("poe1" "poe2" "poe3" "poe4" "poe5" "poe6" "poe7" "poe8" "poe9" "poe10" "poe11" "poe12" "poe13" "poe14" "poe15" "poe25")
else
    declare -a ns=("ahsoka1" "ahsoka2" "ahsoka3" "ahsoka4" "ahsoka5" "ahsoka6" "ahsoka7" "ahsoka8" "ahsoka9" "ahsoka10" "ahsoka11" "ahsoka12" "ahsoka13" "ahsoka14" "ahsoka15" "ahsoka20")
fi

for i in "${ns[@]}"
do
   echo "$i"
   oc project "$i"
   oc get pods
done
