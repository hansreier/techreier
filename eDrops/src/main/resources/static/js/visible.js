function visible() {
    // console.log("Visible function executed");
    const passwordInput = document.getElementById("password");
    const eyeIcon = document.getElementById("togglePassword");
    const isPasswordVisible = passwordInput.type === "text";

    if (isPasswordVisible) {
        passwordInput.type = "password";
        eyeIcon.textContent = "👁️";
    } else {
        passwordInput.type = "text";
        eyeIcon.textContent = "🔒";
    }
}
