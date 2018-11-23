# X-CREATE
XaCml REquests derivAtion for TEsting


X-CREATE (XaCml REquests derivAtion for TEsting) is a tool for the automated derivation of a test suite starting from an XACML policy. X-CREATE implements different strategies for deriving XACML requests. These strategies are based on combinatorial analysis of the values specified in the XACML policy. X-CREATE also derives a test suite, covering the XACML Context Schema that describes the overall structure of the XACML input requests. The aim of the derived XACML requests is twofold: testing of policy evaluation engines and testing of access control policies. The tool consists of three main components: an intermediate-request generator, which is based on the XML Partition Testing (XPT) approach for intermediate instances (request structures) generation; a policy analyzer which selects the input values from the policy specification and a values manager, which distributes the input values to the request structures. 
