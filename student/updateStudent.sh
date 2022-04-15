
originPrefix="std-1"


for f in $(ls -R **/*.yaml)
do 
    if [ $f != "kustomization.yaml" ]
    then
        echo $f
        sed -i ''  "s/$originPrefix/$PREFIX/" $f
    fi
done 


