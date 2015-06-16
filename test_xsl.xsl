<?xml version="1.0" encoding="UTF-8"?>
<html xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsl:version="1.0"> 
    <head><title> metric_data </title></head> 
	<body> 
		<xsl:for-each select="//noticia"> 
			<table border="1"> 
				<xsl:for-each select="./noticia"> 
					<tr>
						<td> <xsl:value-of select="data"/>  </td>
					</tr> 
				</xsl:for-each> 
			</table> 
		</xsl:for-each> 
	</body> 
</html> 		
