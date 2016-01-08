function loginFormValidate() {

	if (document.LoginForm.j_username.value == "") {

		document.getElementById('errfn').innerHTML = "Please enter username and password";
		document.getElementById('errfn').style.display = "block";

		LoginForm.j_username.focus();
		document.getElementById('globalMessages').style.display = "none";
		document.getElementById('loginError').getElementsByTagName('span')[1].innerHTML = 'Required *';
		document.getElementById('loginError').style.color = "#c60300";
		if (document.LoginForm.j_password.value == "") {
			document.getElementById('passwordError').style.color = "#c60300";
			document.getElementById('passwordError').getElementsByTagName('span')[1].innerHTML = 'Required *';
		}

		return false;
	}
	if (document.LoginForm.j_password.value == "") {

		document.getElementById('errfn').innerHTML = "Please enter username and password";
		document.getElementById('errfn').style.display = "block";

		LoginForm.j_password.focus();
		document.getElementById('globalMessages').style.display = "none";
		document.getElementById('passwordError').style.color = "#c60300";
		document.getElementById('passwordError').getElementsByTagName('span')[1].innerHTML = 'Required *';
		if (document.LoginForm.j_username.value == "") {
			document.getElementById('loginError').style.color = "#c60300";
		}
		return false;
	}

}