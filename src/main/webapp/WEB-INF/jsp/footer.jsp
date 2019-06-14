<!DOCTYPE html>
<html>
<style>
#foot{
	background-color: maroon;
	color: rgb(225,225,225);
	font-style: normal;
	font-size: 13px;
	width: 100%;
	text-align: center;
	padding-top: 10px;
	padding-bottom: 20px;
	bottom: 0px;
	position: sticky;
	margin-top: 15px;
}

#footerlink:link, #footerlink:active, #footerlink:visited {color: rgb(225,225,225);
		text-decoration: none;}
#license:link,#license:active, 
#license:visited {color: rgb(225,225,225);
		text-decoration: underline;}
#footerlink:hover, #license:hover{
		color: white;
		text-decoration: underline;
}

</style>

<footer style="">
<%@ page import="java.util.*"%>
<div class="container" id="foot">
<%Calendar calendar = new GregorianCalendar(); %>
<%Date date = new Date(); %>
<%calendar.setTime(date); %>
<%int currentYear = calendar.get(Calendar.YEAR); %>
<p id="copyright">This work is licensed under a <a id="license" rel="license"
href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative
Commons Attribution-NonCommercial-ShareAlike 4.0 International License</a>
<br>&copy;2013-<%=currentYear%> - 
<a id="footerlink" href="http://ancientgraffiti.org/about/terms-of-use">Terms of Use</a>
<br><a id="footerlink" href="http://ancientgraffiti.org/about/main/versions/version-2-0-0/">Version 2.0.0</a></p>
</div>
</footer>
</html>