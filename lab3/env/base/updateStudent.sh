
originPrefix="std-1"


for f in $(ls *.yaml)
do 
    if [ $f != "kustomization.yaml" ] && [ $f != "rt-inventory-dev-environment.yaml" ]
    then
        echo $f
        sed -i ''  "s/$originPrefix/$PREFIX/" $f
    fi
done 


