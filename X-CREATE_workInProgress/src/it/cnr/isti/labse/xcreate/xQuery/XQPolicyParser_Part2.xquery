")/child::*[1]
let $rlist := $root/descendant::VariableDefinition
let $d1 := data($rlist/@VariableId)
for $condition in $root/descendant::Condition
for $target in $condition/descendant::VariableReference
let $d2 := data($target/@VariableId)
where ( $d1 eq $d2 )
return (update replace $target with $rlist/child::*)
,
(:::::   UPDATE AttributeSelector   ::::)
let $root := doc("