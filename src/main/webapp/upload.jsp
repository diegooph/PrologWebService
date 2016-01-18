<form enctype="multipart/form-data" action="<%=request.getContextPath()%>/rest/import"
	method="POST">
		<input name="file" type="file" />
		<br /> <br />
		<input type="submit" value="Enviar arquivo" />
</form>