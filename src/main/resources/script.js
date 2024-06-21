function toggleDisplay(id) {
  var imgContainer = document.getElementById(id);
  var step = document.getElementById(id+'step');
  if (step == null) return;
  if (imgContainer != null) {
    if (imgContainer.style.display === "" || imgContainer.style.display === "none") {
      imgContainer.style.display = "grid";
    } else {
      imgContainer.style.display = "none";
    }
  }
  if (step != null) {
    if(step.style.display === "" || step.style.display === "none") {
      step.style.display = "grid";
    } else {
      step.style.display = "none";
    }
  }
}

function approve(id, goldenPath, resultPath) {
  var cmdContainer = document.getElementById("command-container");
  const p = document.createElement("p");
  p.textContent = "approve: " + id + "; cp " + goldenPath + " " + resultPath + ";";
  cmdContainer.appendChild(p);
}