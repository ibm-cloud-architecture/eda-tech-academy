
originStudent="boyerje"
originPrefix="std-1"

for f in $(ls *.yaml)
do 
    if [ $f != "kustomization.yaml" ] 
    then
        echo $f
        sed -i ''  -e "s/$originStudent/$GIT_ACCOUNT/" -e "s/$originNS/$PREFIX/" $f
    fi
done 


