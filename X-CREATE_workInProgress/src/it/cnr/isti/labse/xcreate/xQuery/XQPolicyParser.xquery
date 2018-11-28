declare default element namespace "urn:oasis:names:tc:xacml:2.0:policy:schema:os";
(:    Prologo Namespaces    :)
declare namespace xs="http://www.w3.org/2001/XMLSchema";
declare namespace xacml="urn:oasis:names:tc:xacml:2.0:policy:schema:os";
declare namespace xacml-context="urn:oasis:names:tc:xacml:2.0:context:schema:os";
declare namespace targetNamespace="urn:oasis:names:tc:xacml:2.0:context:schema:os";
declare namespace schemaLocation="urn:oasis:names:tc:xacml:2.0:policy:schema:os        http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd";
declare namespace xsi="http://www.w3.org/2001/XMLSchema-instance";
(:    Dichiazione Funzioni    :)

(:++++++++++++++++++++++++++++++++++++++++++++++++
		NEW CONDITION PARSER
+++++++++++++++++++++++++++++++++++++++++++++++++++:)
(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)
declare function local:existChildAD($node) as xs:integer{
let $sad := count($node/SubjectAttributeDesignator)
let $rad := count($node/ResourceAttributeDesignator)
let $aad := count($node/ActionAttributeDesignator)
let $ead := count($node/EnvironmentAttributeDesignator)
return $sad+$rad+$aad+$ead
};

declare function local:caso1($node){
if( (local:existChildAD($node) = 1) and (count($node/AttributeValue)))
then 1
else 0
};
declare function local:caso2($node){
if( (local:existChildAD($node) > 1))
then 1
else 0
};
declare function local:caso3($node){
if ((count($node/child::Apply) = 1) and (count($node/child::AttributeValue) = 1) and (local:existChildAD($node/Apply)))
then 1
else 0
};
declare function local:caso4($node){
if ((count($node/child::Apply) = 1) and (count($node/Apply/child::AttributeValue) >= 1) and (local:existChildAD($node)))
then 1
else 0
};
declare function local:caso5($node){

if(	(count($node/child::Apply) = 2)and
	
	((count($node/Apply[1]/AttributeValue)>=1)and
	(count($node/Apply[1]/AttributeValue) = count($node/Apply[1]/child::*))and
	(local:existChildAD($node/Apply[2]) = 1)and
	(local:existChildAD($node/Apply[2]) = count($node/Apply[2]/child::*))) or
	
	((count($node/Apply[2]/AttributeValue)>=1)and
	(count($node/Apply[2]/AttributeValue) = count($node/Apply[2]/child::*))and
	(local:existChildAD($node/Apply[1]) = 1)and
	(local:existChildAD($node/Apply[1]) = count($node/Apply[1]/child::*))) )
then 1
else 0

(:
if ((count($node/child::Apply) = 2) and (count($node/Apply/child::AttributeValue) >= 1) and (local:existChildAD($node/Apply)))
then 1
else 0
:)
};
declare function local:caso6($node){
if ((count($node/child::Apply) = 1) and (local:existChildAD($node)) and (local:existChildAD($node/Apply)))
then 1
else 0
};
declare function local:caso7($node){
if( (local:existChildAD($node) = 1) and ( (count($node/child::*) - count($node/child::Function)) = local:existChildAD($node)))
then 1
else 0
};
declare function local:caso8($node){
''
};
declare function local:caso9($node){
''
};
(:...........................:)
declare function local:newApply($apply){
<Apply Function = "{data($apply/@FunctionId)}">{
if(count($apply/Function))
then local:functionParser($apply)
else if(local:caso1($apply))
	then <caso1>{local:makeTuple(local:extractAD($apply), $apply/child::AttributeValue)}</caso1>
	else if(local:caso2($apply))
		then <caso2>{	for $attD in $apply/child::*
						where (fn:contains(local-name($attD), 'AttributeDesignator'))
						return 	local:makeTuple($attD, local:makeAttributeValue('Caso 2'))
					}</caso2>
		else if(local:caso3($apply))
			then <caso3>{local:makeTuple(local:extractAD($apply/Apply), $apply/child::AttributeValue)}</caso3>
			else if(local:caso4($apply))
				then <caso4>{local:makeTuple(local:extractAD($apply), $apply/Apply/child::AttributeValue)}</caso4>
				else if(local:caso5($apply))
				then <caso5>{
							let $attV := for $atV in $apply/Apply/child::AttributeValue return $atV
							let $applyAttD := for $applyAD in $apply/Apply where local:extractAD($applyAD) return $applyAD
							return local:makeTuple(local:extractAD($applyAttD), $attV)}</caso5>
				else if(local:caso6($apply))
					then <caso6>{(local:makeTuple(local:extractAD($apply),local:makeAttributeValue('caso6') ),
								local:makeTuple(local:extractAD($apply/Apply),local:makeAttributeValue('caso6') ))
							}</caso6>
					else if(local:caso7($apply))
					then <caso7>{
							local:makeTuple(local:extractAD($apply), local:makeAttributeValue('Caso 7'))
							}</caso7>
					else for $applyChild in $apply/child::Apply
						return local:newApply($applyChild)
				
}</Apply>
};
declare function local:newCondition($condition){
if(local:caso1($condition))
then local:functionParser($condition)
else <Condition Figli="{count($condition/Function)}">{
    	for $apply in $condition/child::Apply
	    return local:newApply($apply)
	}</Condition>
};
(:CASO PRESENZA DI FUNCTION:)
declare function local:functionParser($node){
if (:Caso 1:)(local:caso1($node))
then <Function Caso ="Caso 1" Function = "{data($node/Function/@FunctionId)}">{
		local:makeTuple(local:extractAD($node), $node/child::AttributeValue)
	}</Function>
else (:Caso 2:)
	if(local:caso2($node))
	then <Function Caso ="Caso 2" Function = "{data($node/Function/@FunctionId)}">{
			for $attD in $node/child::*
			where fn:contains(local-name($attD), 'AttributeDesignator')
			return 	local:makeTuple($attD, local:makeAttributeValue('Caso2'))
		}</Function>
	else (:Caso 3:) if(local:caso3($node))
		then <Function Caso ="Caso 3" Function = "{data($node/Function/@FunctionId)}">{
				local:makeTuple(local:extractAD($node/Apply), $node/child::AttributeValue)
			}</Function>
		else (:Caso 4:) if(local:caso4($node))
					then <Function Caso ="Caso 4" Function = "{data($node/Function/@FunctionId)}">{
						local:makeTuple(local:extractAD($node), $node/Apply/child::AttributeValue)
						}</Function>
					else (:Caso 5:) if(local:caso5($node))
					then <Function Caso ="Caso 5" Function = "{data($node/Function/@FunctionId)}">{
							let $attV := for $atV in $node/Apply/child::AttributeValue return $atV
							let $applyAttD := for $applyAD in $node/Apply where local:extractAD($applyAD) return $applyAD
							return local:makeTuple(local:extractAD($applyAttD), $attV)
						}</Function>
					else (:Caso 6:) if(local:caso6($node))
							then <Function Caso ="Caso 6" Function = "{data($node/Function/@FunctionId)}">{
								(local:makeTuple(local:extractAD($node),local:makeAttributeValue('caso6') ),
								local:makeTuple(local:extractAD($node/Apply),local:makeAttributeValue('caso6') ))
								}</Function>
							else ''(:"local:functionParser($node)":)
};


declare function local:makeAttributeValue($value as xs:string){<AttributeValue>{$value}</AttributeValue>};

declare function local:makeAttributeValue(){<AttributeValue>Caso 2</AttributeValue>};

declare function local:extractAD($node){
let $sad := count($node/SubjectAttributeDesignator)
let $rad := count($node/ResourceAttributeDesignator)
let $aad := count($node/ActionAttributeDesignator)
let $ead := count($node/EnvironmentAttributeDesignator)
return if($sad)
then $node/SubjectAttributeDesignator
else if($rad)
then $node/ResourceAttributeDesignator
else if($aad)
then $node/ActionAttributeDesignator
else $node/EnvironmentAttributeDesignator
};


declare function local:makeTuple($attD, $attV){
for $aD in $attD
let $aDName := local-name($aD)
return
	if(fn:compare($aDName,'SubjectAttributeDesignator') = 0)
	then <SubjectSet nome="{$aDName}">{ 
		for $aV in $attV
		return local:makeTupla($aD,$aV)}</SubjectSet>
	else if(fn:compare($aDName,'ResourceAttributeDesignator') = 0)
		then <ResourceSet nome="{$aDName}">{ 
			for $aV in $attV
			return local:makeTupla($aD,$aV)}</ResourceSet>
		else if(fn:compare($aDName,'ActionAttributeDesignator') = 0)
			then <ActionSet nome="{$aDName}">{
				for $aV in $attV
				return local:makeTupla($aD,$aV)}</ActionSet>
			else <EnvironmentSet nome="{$aDName}">{
				for $aV in $attV
				return local:makeTupla($aD,$aV)}</EnvironmentSet>
};
(:
declare function local:makeTupla($attD,$aV){
<Tupla>
{$aV}
<AttributeId>{data($attD/@AttributeId)}</AttributeId>
<DataType>{data($attD/@DataType)}</DataType>
{ for $issuer in data($attD/@Issuer) return <Issuer>{$issuer}</Issuer>}
{ for $subCat in  data($attD/@SubjectCategory) return <SubjectCategory>{$subCat}</SubjectCategory>}
</Tupla>
};
:)
(:
declare function local:makeTupla($attD, $attV){
<Tupla>
{$attV}
<AttributeId>{data($attD/@AttributeId)}</AttributeId>
<DataType>{data($attD/@DataType)}</DataType>
{ for $issuer in data($attD/@Issuer) return <Issuer>{$issuer}</Issuer>}
{ for $subCat in  data($attD/@SubjectCategory) return <SubjectCategory>{$subCat}</SubjectCategory>}
</Tupla>
};
:)

(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     PARSING DEL NODO SUBJECTMATCH
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)
declare function local:makeTupla($attD, $attV){
<Tupla>
{$attV}
<AttributeId>{data($attD/@AttributeId)}</AttributeId>
<DataType>{data($attD/@DataType)}</DataType>
{ for $issuer in data($attD/@Issuer) return <Issuer>{$issuer}</Issuer>}
{ for $subCat in  data($attD/@SubjectCategory) return <SubjectCategory>{$subCat}</SubjectCategory>}
</Tupla>
};

declare function local:matchParser($matchNode, $attD){
for $attrValue in $matchNode/AttributeValue
return local:makeTupla($attD, $attrValue)
};

declare function local:matchParser($match){
if ((local-name($match) eq 'SubjectMatch'))
then local:matchParser($match, $match/SubjectAttributeDesignator)
else     if ((local-name($match) eq 'ResourceMatch'))
        then local:matchParser($match, $match/ResourceAttributeDesignator)
        else     if ((local-name($match) eq 'ActionMatch'))
            then local:matchParser($match, $match/ActionAttributeDesignator)
            else local:matchParser($match, $match/EnvironmentAttributeDesignator)
};

(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     PARSING DEL NODO TARGET
     SOLO IL NODO TARGET DETIENE LE INFORMAZIONI DA ESTRARRE
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)
declare function local:targetParser($targetNode){
<RisultatoTarget>{
    let $quantiTargetChild := count($targetNode/child::*)
    return     if($quantiTargetChild)
            then (
                if(count($targetNode/descendant::SubjectMatch))
                then <SubjectSet>{
                        for $subjectMatch in $targetNode/descendant::SubjectMatch
                        return local:matchParser($subjectMatch)
                    }</SubjectSet>
                else <AnySubject>qualsiasi Subject</AnySubject>,
                if(count($targetNode/descendant::ResourceMatch))
                then <ResourceSet>{
                        for $resourceMatch in $targetNode/descendant::ResourceMatch
                        return local:matchParser($resourceMatch)
                    }</ResourceSet>
                else <AnyResource>qualsiasi Resource</AnyResource>,
                if(count($targetNode/descendant::ActionMatch))
                then <ActionSet>{
                        for $actionMatch in $targetNode/descendant::ActionMatch
                        return local:matchParser($actionMatch)
                    }</ActionSet>
                else <AnyAction>qualsiasi Action</AnyAction>,
                if(count($targetNode/descendant::EnvironmentMatch))
                then <EnvironmentSet>{
                        for $environmentMatch in $targetNode/descendant::EnvironmentMatch
                        return local:matchParser($environmentMatch)
                    }</EnvironmentSet>
                else <AnyEnvironment>qualsiasi Environment</AnyEnvironment>)               
            else (<AnySubject>qualsiasi Subject</AnySubject>,
                <AnyResource>qualsiasi Resource</AnyResource>,
                <AnyAction>qualsiasi Action</AnyAction>,
                <AnyEnvironment>qualsiasi Environment</AnyEnvironment>)
}</RisultatoTarget>
};
(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    PARSING DI UN NODO GENERICO : SONO CONSIDERATI SOLO I NODI : POLICYSET, POLICY E RULE.
    SOLO QUESTI NODI HANNO COME FIGLIO DIRETTO UN NODO TARGET
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)
declare function local:nodeParser($nodo){
(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
........     PolicySet
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)
    if(local-name($nodo) eq 'PolicySet')
    then  <PolicySetTarget Id = "{data($nodo/@PolicySetId)}">{
            for $policySetChild in $nodo/child::*
            return    if(local-name($policySetChild) eq 'Target')
                    then local:targetParser($policySetChild)
                    else local:nodeParser($policySetChild)
        }</PolicySetTarget>       
(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
........     Policy
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)
    else
        if(local-name($nodo) eq 'Policy')
        then    <PolicyTarget Id = "{data($nodo/@PolicyId)}">{   
            for $policyChild in $nodo/child::*
            return    if(local-name($policyChild) eq 'Target')
                    then local:targetParser($policyChild)
                    else local:nodeParser($policyChild)
        }</PolicyTarget>                                           
(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
........     Rule <AltroNodo>{local-name($ruleChild)}</AltroNodo>
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)
	    else     if(local-name($nodo) eq 'Rule')
                then     <RuleTarget Id = "{data($nodo/@RuleId)}">{
                    (:Manca Qualcosa
                        se non ha target non popola il risultato con anyALL
                    :)
                        for $ruleChild in $nodo/child::*
                        return    if(local-name($ruleChild) eq 'Target')
                                then local:targetParser($ruleChild)
                                else    if(local-name($ruleChild) eq 'Condition')
                                        then local:newCondition($ruleChild)
                                        else ''
                    }</RuleTarget>
                else ' '
};
(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    INIZIO QUERY
    CARICA FILE DELLA POLITICA
    CHIAMA IL PARSING DEL NODO ROOT DELLA POLITICA
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)

(:<Files>{
for $file in collection()
return <File NomeFile = "{util:document-name($file)}">{$file}</File>
}
</Files>
,:)

(:::::   UPDATE VariableDefinition   ::::)
let $root := doc("top-level.xml")/child::*[1]
let $rlist := $root/descendant::VariableDefinition
let $d1 := data($rlist/@VariableId)
for $condition in $root/descendant::Condition
for $target in $condition/descendant::VariableReference
let $d2 := data($target/@VariableId)
where ( $d1 eq $d2 )
return (update replace $target with $rlist/child::*)
,
(:::::   UPDATE AttributeSelector   ::::)
let $root := doc("top-level.xml")/child::*[1]
for $attSel in $root/descendant::AttributeSelector
return 
	if(fn:contains(data($attSel/@RequestContextPath), "Subject"))
	then update replace $attSel with <SubjectAttributeDesignator DataType = "{data($attSel/@DataType)}" AttributeId =""> </SubjectAttributeDesignator>
	else if (fn:contains(data($attSel/@RequestContextPath), "Resource"))
		then update replace $attSel with <ResourceAttributeDesignator DataType = "{data($attSel/@DataType)}" AttributeId =""> </ResourceAttributeDesignator>
		else if (fn:contains(data($attSel/@RequestContextPath), "Action"))
			then update replace $attSel with <ActionAttributeDesignator DataType = "{data($attSel/@DataType)}" AttributeId =""> </ActionAttributeDesignator>
			else if (fn:contains(data($attSel/@RequestContextPath), "Environment"))
				then update replace $attSel with <EnvironmentAttributeDesignator DataType = "{data($attSel/@DataType)}" AttributeId =""> </EnvironmentAttributeDesignator>
				else update replace $attSel with <ResourceAttributeDesignator DataType = "{data($attSel/@DataType)}" AttributeId =""> </ResourceAttributeDesignator>
		
,
let $root := doc("top-level.xml")/child::*[1]
return local:nodeParser($root)
