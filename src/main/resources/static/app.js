const form = document.querySelector("#description-form");
const image = document.querySelector("#image");
const button = form.querySelector("button");
const controls = [image, document.querySelector("#question"), button];
const status = document.querySelector("#status");
const result = document.querySelector("#result");
const description = document.querySelector(".description");
const speechButton = document.querySelector("#speech-button");

let utterance = null;

// Fonction pour basculer entre lancer et arrêter la lecture
function toggleSpeech() {
    const text = result.textContent;
    if (!text || text.trim() === "") {
        return;
    }

    // Si déjà en cours de lecture, arrêter
    if (speechSynthesis.speaking) {
        speechSynthesis.cancel();
        return;
    }

    // Sinon, lancer la lecture
    utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = "fr-FR";
    speechSynthesis.speak(utterance);
}

// Met à jour l'état du bouton en fonction du contenu
function updateSpeechButton() {
    const hasContent = result.textContent && result.textContent.trim() !== "";
    speechButton.disabled = !hasContent;
}

// Écouteur pour le bouton
speechButton.addEventListener("click", toggleSpeech);

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
      "L'image dépasse la limite de 10 mégaoctets. Choisissez une image plus petite.";
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
    "Impossible d'obtenir une description pour le moment. Réessayez dans un instant.";
  try {
    const response = await fetch("/api/describe", { method: "POST", body });
    if (!response.ok) {
      errorMessage = await response.text()
      throw new Error();
    }

    const data = await response.json();
    if (typeof data.texte !== "string" || !data.texte.trim()) {
      throw new Error("Aucune description reçue. Réessayez.");
    }
    result.textContent = data.texte;
    status.textContent = "Description terminée.";
    updateSpeechButton();
  } catch (error) {
    status.textContent = error instanceof TypeError
      ? "Impossible de joindre le serveur. Réessayez."
      : error.message || errorMessage;
    status.classList.add("error");
    updateSpeechButton();
  } finally {
    controls.forEach((control) => {
      control.disabled = false;
    });
    description.setAttribute("aria-busy", "false");
  }
});
