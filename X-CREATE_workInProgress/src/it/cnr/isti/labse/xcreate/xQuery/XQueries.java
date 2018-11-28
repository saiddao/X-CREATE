package it.cnr.isti.labse.xcreate.xQuery;

public class XQueries {

	public static String getXQPolicyParser_Part1() {

		StringBuilder xQueryBuilder = new StringBuilder();

		xQueryBuilder.append("declare default element namespace \"urn:oasis:names:tc:xacml:2.0:policy:schema:os\";\n");
		xQueryBuilder.append("(:    Prologo Namespaces    :)\n");
		xQueryBuilder.append("declare namespace xs=\"http://www.w3.org/2001/XMLSchema\";\n");
		xQueryBuilder.append("declare namespace xacml=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\";\n");
		xQueryBuilder.append("declare namespace xacml-context=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\";\n");
		xQueryBuilder.append("declare namespace targetNamespace=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\";\n");
		xQueryBuilder.append("declare namespace schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os        http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\";\n");
		xQueryBuilder.append("declare namespace xsi=\"http://www.w3.org/2001/XMLSchema-instance\";\n");
		xQueryBuilder.append("(:    Dichiazione Funzioni    :)\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("(:++++++++++++++++++++++++++++++++++++++++++++++++\n");
		xQueryBuilder.append("		NEW CONDITION PARSER\n");
		xQueryBuilder.append("+++++++++++++++++++++++++++++++++++++++++++++++++++:)\n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("declare function local:existChildAD($node) as xs:integer{\n");
		xQueryBuilder.append("let $sad := count($node/SubjectAttributeDesignator)\n");
		xQueryBuilder.append("let $rad := count($node/ResourceAttributeDesignator)\n");
		xQueryBuilder.append("let $aad := count($node/ActionAttributeDesignator)\n");
		xQueryBuilder.append("let $ead := count($node/EnvironmentAttributeDesignator)\n");
		xQueryBuilder.append("return $sad+$rad+$aad+$ead\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("declare function local:caso1($node){\n");
		xQueryBuilder.append("if( (local:existChildAD($node) = 1) and (count($node/AttributeValue)))\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso2($node){\n");
		xQueryBuilder.append("if( (local:existChildAD($node) > 1))\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso3($node){\n");
		xQueryBuilder.append("if ((count($node/child::Apply) = 1) and (count($node/child::AttributeValue) = 1) and (local:existChildAD($node/Apply)))\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso4($node){\n");
		xQueryBuilder.append("if ((count($node/child::Apply) = 1) and (count($node/Apply/child::AttributeValue) >= 1) and (local:existChildAD($node)))\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso5($node){\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("if(	(count($node/child::Apply) = 2)and\n");
		xQueryBuilder.append("	\n");
		xQueryBuilder.append("	((count($node/Apply[1]/AttributeValue)>=1)and\n");
		xQueryBuilder.append("	(count($node/Apply[1]/AttributeValue) = count($node/Apply[1]/child::*))and\n");
		xQueryBuilder.append("	(local:existChildAD($node/Apply[2]) = 1)and\n");
		xQueryBuilder.append("	(local:existChildAD($node/Apply[2]) = count($node/Apply[2]/child::*))) or\n");
		xQueryBuilder.append("	\n");
		xQueryBuilder.append("	((count($node/Apply[2]/AttributeValue)>=1)and\n");
		xQueryBuilder.append("	(count($node/Apply[2]/AttributeValue) = count($node/Apply[2]/child::*))and\n");
		xQueryBuilder.append("	(local:existChildAD($node/Apply[1]) = 1)and\n");
		xQueryBuilder.append("	(local:existChildAD($node/Apply[1]) = count($node/Apply[1]/child::*))) )\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("(:\n");
		xQueryBuilder.append("if ((count($node/child::Apply) = 2) and (count($node/Apply/child::AttributeValue) >= 1) and (local:existChildAD($node/Apply)))\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append(":)\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso6($node){\n");
		xQueryBuilder.append("if ((count($node/child::Apply) = 1) and (local:existChildAD($node)) and (local:existChildAD($node/Apply)))\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso7($node){\n");
		xQueryBuilder.append("if( (local:existChildAD($node) = 1) and ( (count($node/child::*) - count($node/child::Function)) = local:existChildAD($node)))\n");
		xQueryBuilder.append("then 1\n");
		xQueryBuilder.append("else 0\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso8($node){\n");
		xQueryBuilder.append("''\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:caso9($node){\n");
		xQueryBuilder.append("''\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("(:...........................:)\n");
		xQueryBuilder.append("declare function local:newApply($apply){\n");
		xQueryBuilder.append("<Apply Function = \"{data($apply/@FunctionId)}\">{\n");
		xQueryBuilder.append("if(count($apply/Function))\n");
		xQueryBuilder.append("then local:functionParser($apply)\n");
		xQueryBuilder.append("else if(local:caso1($apply))\n");
		xQueryBuilder.append("	then <caso1>{local:makeTuple(local:extractAD($apply), $apply/child::AttributeValue)}</caso1>\n");
		xQueryBuilder.append("	else if(local:caso2($apply))\n");
		xQueryBuilder.append("		then <caso2>{	for $attD in $apply/child::*\n");
		xQueryBuilder.append("						where (fn:contains(local-name($attD), 'AttributeDesignator'))\n");
		xQueryBuilder.append("						return 	local:makeTuple($attD, local:makeAttributeValue('caso2'))\n");
		xQueryBuilder.append("					}</caso2>\n");
		xQueryBuilder.append("		else if(local:caso3($apply))\n");
		xQueryBuilder.append("			then <caso3>{local:makeTuple(local:extractAD($apply/Apply), $apply/child::AttributeValue)}</caso3>\n");
		xQueryBuilder.append("			else if(local:caso4($apply))\n");
		xQueryBuilder.append("				then <caso4>{local:makeTuple(local:extractAD($apply), $apply/Apply/child::AttributeValue)}</caso4>\n");
		xQueryBuilder.append("				else if(local:caso5($apply))\n");
		xQueryBuilder.append("				then <caso5>{\n");
		xQueryBuilder.append("							let $attV := for $atV in $apply/Apply/child::AttributeValue return $atV\n");
		xQueryBuilder.append("							let $applyAttD := for $applyAD in $apply/Apply where local:extractAD($applyAD) return $applyAD\n");
		xQueryBuilder.append("							return local:makeTuple(local:extractAD($applyAttD), $attV)}</caso5>\n");
		xQueryBuilder.append("				else if(local:caso6($apply))\n");
		xQueryBuilder.append("					then <caso6>{(local:makeTuple(local:extractAD($apply),local:makeAttributeValue('caso6') ),\n");
		xQueryBuilder.append("								local:makeTuple(local:extractAD($apply/Apply),local:makeAttributeValue('caso6') ))\n");
		xQueryBuilder.append("							}</caso6>\n");
		xQueryBuilder.append("					else if(local:caso7($apply))\n");
		xQueryBuilder.append("					then <caso7>{\n");
		xQueryBuilder.append("							local:makeTuple(local:extractAD($apply), local:makeAttributeValue('caso7'))\n");
		xQueryBuilder.append("							}</caso7>\n");
		xQueryBuilder.append("					else for $applyChild in $apply/child::Apply\n");
		xQueryBuilder.append("						return local:newApply($applyChild)\n");
		xQueryBuilder.append("				\n");
		xQueryBuilder.append("}</Apply>\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("declare function local:newCondition($condition){\n");
		xQueryBuilder.append("if(local:caso1($condition))\n");
		xQueryBuilder.append("then local:functionParser($condition)\n");
		xQueryBuilder.append("else <Condition Figli=\"{count($condition/Function)}\">{\n");
		xQueryBuilder.append("    	for $apply in $condition/child::Apply\n");
		xQueryBuilder.append("	    return local:newApply($apply)\n");
		xQueryBuilder.append("	}</Condition>\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("(:CASO PRESENZA DI FUNCTION:)\n");
		xQueryBuilder.append("declare function local:functionParser($node){\n");
		xQueryBuilder.append("if (:Caso 1:)(local:caso1($node))\n");
		xQueryBuilder.append("then <Function Caso =\"Caso1\" Function = \"{data($node/Function/@FunctionId)}\">{\n");
		xQueryBuilder.append("		local:makeTuple(local:extractAD($node), $node/child::AttributeValue)\n");
		xQueryBuilder.append("	}</Function>\n");
		xQueryBuilder.append("else (:Caso 2:)\n");
		xQueryBuilder.append("	if(local:caso2($node))\n");
		xQueryBuilder.append("	then <Function Caso =\"Caso2\" Function = \"{data($node/Function/@FunctionId)}\">{\n");
		xQueryBuilder.append("			for $attD in $node/child::*\n");
		xQueryBuilder.append("			where fn:contains(local-name($attD), 'AttributeDesignator')\n");
		xQueryBuilder.append("			return 	local:makeTuple($attD, local:makeAttributeValue('caso2'))\n");
		xQueryBuilder.append("		}</Function>\n");
		xQueryBuilder.append("	else (:Caso 3:) if(local:caso3($node))\n");
		xQueryBuilder.append("		then <Function Caso =\"Caso3\" Function = \"{data($node/Function/@FunctionId)}\">{\n");
		xQueryBuilder.append("				local:makeTuple(local:extractAD($node/Apply), $node/child::AttributeValue)\n");
		xQueryBuilder.append("			}</Function>\n");
		xQueryBuilder.append("		else (:Caso 4:) if(local:caso4($node))\n");
		xQueryBuilder.append("					then <Function Caso =\"Caso4\" Function = \"{data($node/Function/@FunctionId)}\">{\n");
		xQueryBuilder.append("						local:makeTuple(local:extractAD($node), $node/Apply/child::AttributeValue)\n");
		xQueryBuilder.append("						}</Function>\n");
		xQueryBuilder.append("					else (:Caso 5:) if(local:caso5($node))\n");
		xQueryBuilder.append("					then <Function Caso =\"Caso5\" Function = \"{data($node/Function/@FunctionId)}\">{\n");
		xQueryBuilder.append("							let $attV := for $atV in $node/Apply/child::AttributeValue return $atV\n");
		xQueryBuilder.append("							let $applyAttD := for $applyAD in $node/Apply where local:extractAD($applyAD) return $applyAD\n");
		xQueryBuilder.append("							return local:makeTuple(local:extractAD($applyAttD), $attV)\n");
		xQueryBuilder.append("						}</Function>\n");
		xQueryBuilder.append("					else (:Caso 6:) if(local:caso6($node))\n");
		xQueryBuilder.append("							then <Function Caso =\"Caso6\" Function = \"{data($node/Function/@FunctionId)}\">{\n");
		xQueryBuilder.append("								(local:makeTuple(local:extractAD($node),local:makeAttributeValue('caso6') ),\n");
		xQueryBuilder.append("								local:makeTuple(local:extractAD($node/Apply),local:makeAttributeValue('caso6') ))\n");
		xQueryBuilder.append("								}</Function>\n");
		xQueryBuilder.append("							else ''(:\"local:functionParser($node)\":)\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("declare function local:makeAttributeValue($value as xs:string){<AttributeValue>{$value}</AttributeValue>};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("declare function local:makeAttributeValue(){<AttributeValue>caso2</AttributeValue>};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("declare function local:extractAD($node){\n");
		xQueryBuilder.append("let $sad := count($node/SubjectAttributeDesignator)\n");
		xQueryBuilder.append("let $rad := count($node/ResourceAttributeDesignator)\n");
		xQueryBuilder.append("let $aad := count($node/ActionAttributeDesignator)\n");
		xQueryBuilder.append("let $ead := count($node/EnvironmentAttributeDesignator)\n");
		xQueryBuilder.append("return if($sad)\n");
		xQueryBuilder.append("then $node/SubjectAttributeDesignator\n");
		xQueryBuilder.append("else if($rad)\n");
		xQueryBuilder.append("then $node/ResourceAttributeDesignator\n");
		xQueryBuilder.append("else if($aad)\n");
		xQueryBuilder.append("then $node/ActionAttributeDesignator\n");
		xQueryBuilder.append("else $node/EnvironmentAttributeDesignator\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("declare function local:makeTuple($attD, $attV){\n");
		xQueryBuilder.append("for $aD in $attD\n");
		xQueryBuilder.append("let $aDName := local-name($aD)\n");
		xQueryBuilder.append("return\n");
		xQueryBuilder.append("	if(fn:compare($aDName,'SubjectAttributeDesignator') = 0)\n");
		xQueryBuilder.append("	then <SubjectSet nome=\"{$aDName}\">{ \n");
		xQueryBuilder.append("		for $aV in $attV\n");
		xQueryBuilder.append("		return local:makeTupla($aD,$aV)}</SubjectSet>\n");
		xQueryBuilder.append("	else if(fn:compare($aDName,'ResourceAttributeDesignator') = 0)\n");
		xQueryBuilder.append("		then <ResourceSet nome=\"{$aDName}\">{ \n");
		xQueryBuilder.append("			for $aV in $attV\n");
		xQueryBuilder.append("			return local:makeTupla($aD,$aV)}</ResourceSet>\n");
		xQueryBuilder.append("		else if(fn:compare($aDName,'ActionAttributeDesignator') = 0)\n");
		xQueryBuilder.append("			then <ActionSet nome=\"{$aDName}\">{\n");
		xQueryBuilder.append("				for $aV in $attV\n");
		xQueryBuilder.append("				return local:makeTupla($aD,$aV)}</ActionSet>\n");
		xQueryBuilder.append("			else <EnvironmentSet nome=\"{$aDName}\">{\n");
		xQueryBuilder.append("				for $aV in $attV\n");
		xQueryBuilder.append("				return local:makeTupla($aD,$aV)}</EnvironmentSet>\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("(:\n");
		xQueryBuilder.append("declare function local:makeTupla($attD,$aV){\n");
		xQueryBuilder.append("<Tupla>\n");
		xQueryBuilder.append("{$aV}\n");
		xQueryBuilder.append("<AttributeId>{data($attD/@AttributeId)}</AttributeId>\n");
		xQueryBuilder.append("<DataType>{data($attD/@DataType)}</DataType>\n");
		xQueryBuilder.append("{ for $issuer in data($attD/@Issuer) return <Issuer>{$issuer}</Issuer>}\n");
		xQueryBuilder.append("{ for $subCat in  data($attD/@SubjectCategory) return <SubjectCategory>{$subCat}</SubjectCategory>}\n");
		xQueryBuilder.append("</Tupla>\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append(":)\n");
		xQueryBuilder.append("(:\n");
		xQueryBuilder.append("declare function local:makeTupla($attD, $attV){\n");
		xQueryBuilder.append("<Tupla>\n");
		xQueryBuilder.append("{$attV}\n");
		xQueryBuilder.append("<AttributeId>{data($attD/@AttributeId)}</AttributeId>\n");
		xQueryBuilder.append("<DataType>{data($attD/@DataType)}</DataType>\n");
		xQueryBuilder.append("{ for $issuer in data($attD/@Issuer) return <Issuer>{$issuer}</Issuer>}\n");
		xQueryBuilder.append("{ for $subCat in  data($attD/@SubjectCategory) return <SubjectCategory>{$subCat}</SubjectCategory>}\n");
		xQueryBuilder.append("</Tupla>\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append(":)\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("     PARSING DEL NODO SUBJECTMATCH\n");
		xQueryBuilder.append(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("declare function local:makeTupla($attD, $attV){\n");
		xQueryBuilder.append("<Tupla>\n");
		xQueryBuilder.append("{$attV}\n");
		xQueryBuilder.append("<AttributeId>{data($attD/@AttributeId)}</AttributeId>\n");
		xQueryBuilder.append("<DataType>{data($attD/@DataType)}</DataType>\n");
		xQueryBuilder.append("{ for $issuer in data($attD/@Issuer) return <Issuer>{$issuer}</Issuer>}\n");
		xQueryBuilder.append("{ for $subCat in  data($attD/@SubjectCategory) return <SubjectCategory>{$subCat}</SubjectCategory>}\n");
		xQueryBuilder.append("</Tupla>\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("declare function local:matchParser($matchNode, $attD){\n");
		xQueryBuilder.append("for $attrValue in $matchNode/AttributeValue\n");
		xQueryBuilder.append("return local:makeTupla($attD, $attrValue)\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("declare function local:matchParser($match){\n");
		xQueryBuilder.append("if ((local-name($match) eq 'SubjectMatch'))\n");
		xQueryBuilder.append("then local:matchParser($match, $match/SubjectAttributeDesignator)\n");
		xQueryBuilder.append("else     if ((local-name($match) eq 'ResourceMatch'))\n");
		xQueryBuilder.append("        then local:matchParser($match, $match/ResourceAttributeDesignator)\n");
		xQueryBuilder.append("        else     if ((local-name($match) eq 'ActionMatch'))\n");
		xQueryBuilder.append("            then local:matchParser($match, $match/ActionAttributeDesignator)\n");
		xQueryBuilder.append("            else local:matchParser($match, $match/EnvironmentAttributeDesignator)\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("     PARSING DEL NODO TARGET\n");
		xQueryBuilder.append("     SOLO IL NODO TARGET DETIENE LE INFORMAZIONI DA ESTRARRE\n");
		xQueryBuilder.append(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("declare function local:targetParser($targetNode){\n");
		xQueryBuilder.append("<RisultatoTarget>{\n");
		xQueryBuilder.append("    let $quantiTargetChild := count($targetNode/child::*)\n");
		xQueryBuilder.append("    return     if($quantiTargetChild)\n");
		xQueryBuilder.append("            then (\n");
		xQueryBuilder.append("                if(count($targetNode/descendant::SubjectMatch))\n");
		xQueryBuilder.append("                then <SubjectSet>{\n");
		xQueryBuilder.append("                        for $subjectMatch in $targetNode/descendant::SubjectMatch\n");
		xQueryBuilder.append("                        return local:matchParser($subjectMatch)\n");
		xQueryBuilder.append("                    }</SubjectSet>\n");
		xQueryBuilder.append("                else <AnySubject>qualsiasi Subject</AnySubject>,\n");
		xQueryBuilder.append("                if(count($targetNode/descendant::ResourceMatch))\n");
		xQueryBuilder.append("                then <ResourceSet>{\n");
		xQueryBuilder.append("                        for $resourceMatch in $targetNode/descendant::ResourceMatch\n");
		xQueryBuilder.append("                        return local:matchParser($resourceMatch)\n");
		xQueryBuilder.append("                    }</ResourceSet>\n");
		xQueryBuilder.append("                else <AnyResource>qualsiasi Resource</AnyResource>,\n");
		xQueryBuilder.append("                if(count($targetNode/descendant::ActionMatch))\n");
		xQueryBuilder.append("                then <ActionSet>{\n");
		xQueryBuilder.append("                        for $actionMatch in $targetNode/descendant::ActionMatch\n");
		xQueryBuilder.append("                        return local:matchParser($actionMatch)\n");
		xQueryBuilder.append("                    }</ActionSet>\n");
		xQueryBuilder.append("                else <AnyAction>qualsiasi Action</AnyAction>,\n");
		xQueryBuilder.append("                if(count($targetNode/descendant::EnvironmentMatch))\n");
		xQueryBuilder.append("                then <EnvironmentSet>{\n");
		xQueryBuilder.append("                        for $environmentMatch in $targetNode/descendant::EnvironmentMatch\n");
		xQueryBuilder.append("                        return local:matchParser($environmentMatch)\n");
		xQueryBuilder.append("                    }</EnvironmentSet>\n");
		xQueryBuilder.append("                else <AnyEnvironment>qualsiasi Environment</AnyEnvironment>)               \n");
		xQueryBuilder.append("            else (<AnySubject>qualsiasi Subject</AnySubject>,\n");
		xQueryBuilder.append("                <AnyResource>qualsiasi Resource</AnyResource>,\n");
		xQueryBuilder.append("                <AnyAction>qualsiasi Action</AnyAction>,\n");
		xQueryBuilder.append("                <AnyEnvironment>qualsiasi Environment</AnyEnvironment>)\n");
		xQueryBuilder.append("}</RisultatoTarget>\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("    PARSING DI UN NODO GENERICO : SONO CONSIDERATI SOLO I NODI : POLICYSET, POLICY E RULE.\n");
		xQueryBuilder.append("    SOLO QUETI NODI HANNO COME FIGLIO DIRETTO UN NODO TARGET\n");
		xQueryBuilder.append(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("declare function local:nodeParser($nodo){\n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("........     PolicySet\n");
		xQueryBuilder.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("    if(local-name($nodo) eq 'PolicySet')\n");
		xQueryBuilder.append("    then  <PolicySetTarget Id = \"{data($nodo/@PolicySetId)}\">{\n");
		xQueryBuilder.append("            for $policySetChild in $nodo/child::*\n");
		xQueryBuilder.append("            return    if(local-name($policySetChild) eq 'Target')\n");
		xQueryBuilder.append("                    then local:targetParser($policySetChild)\n");
		xQueryBuilder.append("                    else local:nodeParser($policySetChild)\n");
		xQueryBuilder.append("        }</PolicySetTarget>       \n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("........     Policy\n");
		xQueryBuilder.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("    else\n");
		xQueryBuilder.append("        if(local-name($nodo) eq 'Policy')\n");
		xQueryBuilder.append("        then    <PolicyTarget Id = \"{data($nodo/@PolicyId)}\">{   \n");
		xQueryBuilder.append("            for $policyChild in $nodo/child::*\n");
		xQueryBuilder.append("            return    if(local-name($policyChild) eq 'Target')\n");
		xQueryBuilder.append("                    then local:targetParser($policyChild)\n");
		xQueryBuilder.append("                    else local:nodeParser($policyChild)\n");
		xQueryBuilder.append("        }</PolicyTarget>                                           \n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("........     Rule <AltroNodo>{local-name($ruleChild)}</AltroNodo>\n");
		xQueryBuilder.append("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("	    else     if(local-name($nodo) eq 'Rule')\n");
		xQueryBuilder.append("                then     <RuleTarget Id = \"{data($nodo/@RuleId)}\">{\n");
		xQueryBuilder.append("                    (:Manca Qualcosa\n");
		xQueryBuilder.append("                        se non ha target non popola il risultato con anyALL\n");
		xQueryBuilder.append("                    :)\n");
		xQueryBuilder.append("                        for $ruleChild in $nodo/child::*\n");
		xQueryBuilder.append("                        return    if(local-name($ruleChild) eq 'Target')\n");
		xQueryBuilder.append("                                then local:targetParser($ruleChild)\n");
		xQueryBuilder.append("                                else    if(local-name($ruleChild) eq 'Condition')\n");
		xQueryBuilder.append("                                        then local:newCondition($ruleChild)\n");
		xQueryBuilder.append("                                        else ''\n");
		xQueryBuilder.append("                    }</RuleTarget>\n");
		xQueryBuilder.append("                else ' '\n");
		xQueryBuilder.append("};\n");
		xQueryBuilder.append("(:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
		xQueryBuilder.append("    INIZIO QUERY\n");
		xQueryBuilder.append("    CARICA FILE DELLA POLITICA\n");
		xQueryBuilder.append("    CHIAMA IL PARSING DEL NODO ROOT DELLA POLITICA\n");
		xQueryBuilder.append(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::)\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("(:<Files>{\n");
		xQueryBuilder.append("for $file in collection()\n");
		xQueryBuilder.append("return <File NomeFile = \"{util:document-name($file)}\">{$file}</File>\n");
		xQueryBuilder.append("}\n");
		xQueryBuilder.append("</Files>\n");
		xQueryBuilder.append(",:)\n");
		xQueryBuilder.append("\n");
		xQueryBuilder.append("(:::::   UPDATE VariableDefinition   ::::)\n");
		xQueryBuilder.append("let $root := doc(\"");

		return xQueryBuilder.toString();
	}

	public static String getXQPolicyParser_Part2() {
		StringBuilder xQueryBuilder = new StringBuilder();

		xQueryBuilder.append("\")/child::*[1]\n");
		xQueryBuilder.append("let $rlist := $root/descendant::VariableDefinition\n");
		xQueryBuilder.append("let $d1 := data($rlist/@VariableId)\n");
		xQueryBuilder.append("for $condition in $root/descendant::Condition\n");
		xQueryBuilder.append("for $target in $condition/descendant::VariableReference\n");
		xQueryBuilder.append("let $d2 := data($target/@VariableId)\n");
		xQueryBuilder.append("where ( $d1 eq $d2 )\n");
		xQueryBuilder.append("return (update replace $target with $rlist/child::*)\n");
		xQueryBuilder.append(",\n");
		xQueryBuilder.append("(:::::   UPDATE AttributeSelector   ::::)\n");
		xQueryBuilder.append("let $root := doc(\"");

		return xQueryBuilder.toString();
	}

	public static String getXQPolicyParser_Part3() {
		StringBuilder xQueryBuilder = new StringBuilder();

		xQueryBuilder.append("\")/child::*[1]\n");
		xQueryBuilder.append("for $attSel in $root/descendant::AttributeSelector\n");
		xQueryBuilder.append("return \n");
		xQueryBuilder.append("	if(fn:contains(data($attSel/@RequestContextPath), \"Subject\"))\n");
		xQueryBuilder.append("	then update replace $attSel with <SubjectAttributeDesignator DataType = \"{data($attSel/@DataType)}\" AttributeId =\"\"> </SubjectAttributeDesignator>\n");
		xQueryBuilder.append("	else if (fn:contains(data($attSel/@RequestContextPath), \"Resource\"))\n");
		xQueryBuilder.append("		then update replace $attSel with <ResourceAttributeDesignator DataType = \"{data($attSel/@DataType)}\" AttributeId =\"\"> </ResourceAttributeDesignator>\n");
		xQueryBuilder.append("		else if (fn:contains(data($attSel/@RequestContextPath), \"Action\"))\n");
		xQueryBuilder.append("			then update replace $attSel with <ActionAttributeDesignator DataType = \"{data($attSel/@DataType)}\" AttributeId =\"\"> </ActionAttributeDesignator>\n");
		xQueryBuilder.append("			else if (fn:contains(data($attSel/@RequestContextPath), \"Environment\"))\n");
		xQueryBuilder.append("				then update replace $attSel with <EnvironmentAttributeDesignator DataType = \"{data($attSel/@DataType)}\" AttributeId =\"\"> </EnvironmentAttributeDesignator>\n");
		xQueryBuilder.append("				else update replace $attSel with <ResourceAttributeDesignator DataType = \"{data($attSel/@DataType)}\" AttributeId =\"\"> </ResourceAttributeDesignator>\n");
		xQueryBuilder.append("		\n");
		xQueryBuilder.append(",\n");
		xQueryBuilder.append("let $root := doc(\"");

		return xQueryBuilder.toString();
	}

	public static String getXQPolicyParser_Part4() {
		StringBuilder xQueryBuilder = new StringBuilder();
		xQueryBuilder.append("\")/child::*[1]\n");
		xQueryBuilder.append("return local:nodeParser($root)\n");
		return xQueryBuilder.toString();
	}
}