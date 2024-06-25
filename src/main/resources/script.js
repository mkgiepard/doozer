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
  const commands = cmdContainer.textContent;
  var nextCommand = "cp " + goldenPath + " " + resultPath + ";";
  cmdContainer.textContent = commands + nextCommand;
}

function copy() {
  var cmdContainer = document.getElementById("command-container");
  navigator.clipboard.writeText(cmdContainer.textContent);
}

function clearCommands() {
  var cmdContainer = document.getElementById("command-container");
  cmdContainer.textContent = "";
}