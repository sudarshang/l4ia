<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/Document">
    <BooleanQuery>
      <xsl:if test="type">        <!-- #1 -->
        <Clause occurs="must">
          <ConstantScoreQuery>
            <CachedFilter>
              <TermsFilter fieldName="type"><xsl:value-of select="type"/></TermsFilter>
            </CachedFilter>
          </ConstantScoreQuery>
        </Clause>
      </xsl:if>
      
      <xsl:if test="description">  <!-- #2 -->
        <Clause occurs="must">
          <UserQuery fieldName="description">
            <xsl:value-of select="description"/>
          </UserQuery>
        </Clause>
      </xsl:if>      

      <xsl:if test="South|North|East|West">  <!-- #3 -->
        <Clause occurs="must">
          <ConstantScoreQuery>
            <BooleanFilter>
              <xsl:for-each select="South|North|East|West">
                <Clause occurs="should">
                  <CachedFilter>
                    <TermsFilter fieldName="location">
                      <xsl:value-of select="name()"/>
                    </TermsFilter>
                  </CachedFilter>
                </Clause>
              </xsl:for-each>                                        
            </BooleanFilter>
          </ConstantScoreQuery>
        </Clause>
      </xsl:if>
      
      <xsl:if test="salaryRange">  <!-- #4 -->
        <Clause occurs="must">
          <ConstantScoreQuery>
            <RangeFilter fieldName="salary" >
              <xsl:attribute name="lowerTerm">
                <xsl:value-of 
                   select='format-number( substring-before(salaryRange,"-"), "000" )' />
              </xsl:attribute> 
              <xsl:attribute name="upperTerm">
                <xsl:value-of 
                   select='format-number( substring-after(salaryRange,"-"), "000" )' />
              </xsl:attribute> 
            </RangeFilter>
          </ConstantScoreQuery>
        </Clause>
      </xsl:if>        
    </BooleanQuery>
  </xsl:template>
</xsl:stylesheet>

<!--
  #1 If user selects a preference for type of job, apply choice of 
     permanent/contract filter and cache
  #2 Use standard Lucene query parser for any job description input
  #3 If any of the location fields are set OR them ALL in a Boolean
     filter and cache individual filters
  #4 Translate salary range into a constant score range filter
-->
