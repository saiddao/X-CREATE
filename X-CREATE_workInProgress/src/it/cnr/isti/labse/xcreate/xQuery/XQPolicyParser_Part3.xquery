")/child::*[1]
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
let $root := doc("