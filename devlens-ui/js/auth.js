function login() {
  const email = document.getElementById("email").value;

  if (email === "admin@test.com") {
    localStorage.setItem("role", "ADMIN");
    window.location.href = "admin.html";
  } else {
    localStorage.setItem("role", "USER");
    window.location.href = "user.html";
  }
}