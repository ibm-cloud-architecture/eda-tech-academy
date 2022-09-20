# Makefile to support bootstrapping GitOps and deploy apps with GitOps
# or having single target to do in one click without GitOps to monitor the git.

# Setting global variables

# Reusable functions
ensure_operator_installed = \
	OPERATORNAME=$1; \
	OPERATORINSTALLER=$2; \
	ISOPERATORINSTALLED=$$(oc get subscription -n openshift-operators  -o go-template='{{len .items}}' --field-selector metadata.name=$$OPERATORNAME); \
	if [ $$ISOPERATORINSTALLED -eq 0 ]; \
	then \
		oc apply -k $$OPERATORINSTALLER ; \
		QUERY="{.items[0].status.phase}"; \
		sleep 30 ; \
		OPERATORSTATUS=""; \
		while [ "$$OPERATORSTATUS" != "Complete" ]; do \
			OPERATORSTATUS=$$(oc get installplan -n openshift-operators -l operators.coreos.com/$$OPERATORNAME.openshift-operators -o jsonpath="$$QUERY"); \
			echo $$OPERATORSTATUS; \
			sleep 90 ; \
		done; \
	else \
		echo $$OPERATORNAME "Installed"; \
	fi

# ---------------------------------------------------
# GitOps targets
# ---------------------------------------------------
verify-argocd-available:
	@$(call ensure_operator_installed,"openshift-gitops-operator","./argocd/bootstrap/openshift-gitops-operator")

# Create a project in ArgoCD to isolate student
def-argocd-project:
	@oc apply -f argocd/argo-project.yaml

prepare-argocd: 
	@echo $$PREFIX $$GIT_ACCOUNT 
	@cd argocd && ./updateStudent.sh && cd ..

gitops: def-argocd-project
	@oc apply -k argocd/

clean-gitops: 
	@oc delete -k argocd/
	@oc delete -f argocd/argo-project.yaml
# ------------------------------------------------------
# Solution deployment targets
# ------------------------------------------------------

eventstreams-config:
	@oc apply -k services/ibm-eventstreams/overlays

# Prepare student dedicated namespace
prepare-ns:
	@oc apply -k env/overlays
	if [ -z $$PREFIX ]; then echo Use $$PREFIX; else export PREFIX=std-1;fi
	@oc project $$PREFIX

mq-config:
	@oc apply -k services/ibm-mq/overlays

kconnect:
	@oc apply -k ./services/kconnect

mq-kconnector:
	@oc apply -f ./apps/mq-source/kafka-mq-src-connector.yaml

# ----------- app specific -------------
store-simulator:
	@oc apply -k ./apps/store-simulator/

store-inventory:
	@oc apply -k ./apps/store-inventory/

item-inventory:
	@oc apply -k ./apps/item-inventory/

apps: store-simulator \
	store-inventory\
	item-inventory 

all-no-gitops:  prepare-ns\
	eventstreams-config \
	mq-config \
	kconnect \
	apps

clean-serv: 
	@oc delete -k ./services/kconnect
	@oc delete -k ./services/ibm-mq/overlays
	@oc delete -k ./services/ibm-eventstreams/overlays

clean:
	@oc project $$PREFIX
	@oc delete -k ./apps/item-inventory/
	@oc delete -k ./apps/store-inventory/
	@oc delete -k ./services/kconnect
	@oc delete -k ./services/ibm-mq/overlays
	@oc delete -k ./services/ibm-eventstreams/overlays
	@oc delete -k ./apps/store-simulator/
	@oc delete kafkatopic $$PREFIX-connect-cluster-configs -n cp4i-eventstreams
	@oc delete kafkatopic $$PREFIX-connect-cluster-offsets -n cp4i-eventstreams
	@oc delete kafkatopic $$PREFIX-connect-cluster-status -n cp4i-eventstreams
	@oc delete -k ./env/overlays
	
