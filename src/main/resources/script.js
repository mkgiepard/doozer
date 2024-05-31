function toggleDisplay(id) {
  var imgContainer = document.getElementById(id);
  var step = document.getElementById(id+'step');
  if (step == null) return;
  if (imgContainer.style.display === "" || step.style.display === ""
        || imgContainer.style.display === "none" || step.style.display === "none") {
    imgContainer.style.display = "grid";
    step.style.display = "grid";
  } else {
    imgContainer.style.display = "none";
    step.style.display = "none";
  }
} 