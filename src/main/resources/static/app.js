const form = document.querySelector("#description-form");
const image = document.querySelector("#image");
const button = form.querySelector("button");
const status = document.querySelector("#status");
const result = document.querySelector("#result");
const description = document.querySelector(".description");

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    status.classList.remove("error");

    const file = image.files[0];
    if (!file) {
        status.textContent = "Choisissez une image avant de continuer.";
        return;
    }
    if (file.size > 10 * 1024 * 1024) {
        status.textContent = "L’image dépasse la limite de 10 Mo.";
        status.classList.add("error");
        return;
    }

    const body = new FormData(form);
    button.disabled = true;
    image.disabled = true;
    description.setAttribute("aria-busy", "true");
    status.textContent = "Description en cours… Cela peut prendre un moment.";
    result.textContent = "";
    result.classList.remove("placeholder");

    try {
        const response = await fetch("/api/describe", { method: "POST", body });
        if (!response.ok) {
            const message = await response.text();
            const isPlainText = response.headers.get("content-type")?.includes("text/plain");
            throw new Error(
                isPlainText && message ? message : "Impossible de décrire cette image. Réessayez."
            );
        }

        const data = await response.json();
        if (typeof data.texte !== "string" || !data.texte.trim()) {
            throw new Error("Aucune description reçue. Réessayez.");
        }
        result.textContent = data.texte;
        status.textContent = "Description terminée.";
    } catch (error) {
        status.textContent = error instanceof TypeError
            ? "Impossible de joindre le serveur. Réessayez."
            : error.message;
        status.classList.add("error");
    } finally {
        button.disabled = false;
        image.disabled = false;
        description.setAttribute("aria-busy", "false");
    }
});
