<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xmi="http://www.omg.org/spec/XMI" 
                xmlns:uml="http://www.uml.org"
                version="2.0">
    
    <xsl:template match="/package">
        <xmi:XMI xmlns:xmi="http://www.omg.org/spec/XMI" xmlns:uml="http://www.uml.org">
            <xmi:Model xmi:id="model" name="UML Model" type="uml:Model">
                
                <xsl:apply-templates select="class"/>

                <xsl:apply-templates select="interface"/>

                <xsl:apply-templates select="enum"/>
                
            </xmi:Model>
        </xmi:XMI>
    </xsl:template>

    <xsl:template match="class">
        <uml:Class xmi:id="{name}" name="{name}">
            
            <xsl:apply-templates select="fields/field"/>
            
            <xsl:apply-templates select="methods/method"/>
            
            <xsl:apply-templates select="relationships/*"/>

        </uml:Class>
    </xsl:template>

    <xsl:template match="field">
        <uml:Attribute xmi:id="{name}" name="{name}" visibility="{modifier}" type="{type}"/>
    </xsl:template>

    <xsl:template match="method">
        <uml:Operation xmi:id="{name}" name="{name}" visibility="{modifier}" returnType="{returnType}"/>
    </xsl:template>

    <xsl:template match="relationships/*">
        <xsl:choose>
            <xsl:when test="name() = 'parent'">
                <uml:Generalization xmi:id="{concat(../name, '-', @name)}" general="{text()}" specific="{../name}"/>
            </xsl:when>
            <xsl:when test="name() = 'aggregation'">
                <uml:Association xmi:id="{concat(../name, '-', @from, '-', @to)}">
                    <uml:MemberEnd xmi:idref="{@from}"/>
                    <uml:MemberEnd xmi:idref="{@to}"/>
                </uml:Association>
            </xsl:when>
            <xsl:when test="name() = 'composition'">
                <uml:Association xmi:id="{concat(../name, '-', @from, '-', @to)}" composite="true">
                    <uml:MemberEnd xmi:idref="{@from}"/>
                    <uml:MemberEnd xmi:idref="{@to}"/>
                </uml:Association>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="interface">
        <uml:Interface xmi:id="{name}" name="{name}">
            <xsl:apply-templates select="fields/field"/>
            <xsl:apply-templates select="methods/method"/>
        </uml:Interface>
    </xsl:template>

    <xsl:template match="enum">
        <uml:Enumeration xmi:id="{name}" name="{name}">
            <xsl:apply-templates select="fields/field"/>
        </uml:Enumeration>
    </xsl:template>

</xsl:stylesheet>
