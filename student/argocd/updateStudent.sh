
originStudent="student-1"
url="ibm-cloud-architecture"
originPrefix="std-1"

for f in $(ls *.yaml)
do 
    if [ $f != "kustomization.yaml" ] 
    then
        echo $f
        sed -i ''  -e "s/$originStudent/$USER_NAME/" -e "s/$url/$GIT_REPO_NAME/"  -e "s/$originNS/$PREFIX/" $f
    fi
done 


