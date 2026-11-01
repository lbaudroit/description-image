const form = document.querySelector("#description-form");
const image = document.querySelector("#image");
const button = form.querySelector("button");
const controls = [image, document.querySelector("#question"), button];
const status = document.querySelector("#status");
const result = document.querySelector("#result");
const description = document.querySelector(".description");

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (button.disabled) return;
  status.classList.remove("error");

  const file = image.files[0];
  if (!file) {
    status.textContent = "Choisissez une image avant de continuer.";
    status.classList.add("error");
    return;
  }
  if (file.size > 10 * 1024 * 1024) {
    status.textContent =
      "L’image dépasse la limite de 10 Mio. Choisissez une image plus petite.";
    status.classList.add("error");
    return;
  }

  const body = new FormData(form);
  controls.forEach((control) => {
    control.disabled = true;
  });
  description.setAttribute("aria-busy", "true");
  status.textContent = "Description en cours… Cela peut prendre un moment.";
  result.textContent = "";
  result.classList.remove("placeholder");

  let errorMessage =
    "Impossible d’obtenir une description pour le moment. Réessayez dans un instant.";
  try {
    const response = await fetch("/api/describe", { method: "POST", body });
    if (!response.ok) {
      if (response.status === 400) {
        errorMessage =
          "L’image n’a pas pu être acceptée. Choisissez un fichier PNG, JPEG ou GIF non vide, puis réessayez.";
      } else if (response.status === 413) {
        errorMessage =
          "L’image est trop volumineuse. Choisissez une image plus petite (10 Mio maximum).";
      }
      throw new Error();
    }

    const data = await response.json();
    if (typeof data?.texte !== "string" || !data.texte.trim()) {
      throw new Error();
    }
    result.textContent = data.texte;
    status.textContent = "Description terminée.";
  } catch {
    status.textContent = errorMessage;
    status.classList.add("error");
  } finally {
    controls.forEach((control) => {
      control.disabled = false;
    });
    description.setAttribute("aria-busy", "false");
  }
});
