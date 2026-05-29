const STORAGE_KEY = "petshop-mrm-store";
const API_BASE = "http://localhost:8080";
const CATEGORY_URL = `${API_BASE}/categorias`;
const PRODUCT_URL = `${API_BASE}/produtos`;
const ORDER_URL = `${API_BASE}/pedidos`;
const ORDER_ITEM_URL = `${API_BASE}/itens-pedido`;
const USER_ID = 1;

const state = {
  categories: [],
  products: [],
  cart: null,
  mode: "loading",
  selectedCategory: "all",
  search: "",
  currentProductId: null
};

const elements = {
  views: document.querySelectorAll("[data-view]"),
  navButtons: document.querySelectorAll("[data-view-target]"),
  connectionBadge: document.getElementById("connectionBadge"),
  heroCategoryCount: document.getElementById("heroCategoryCount"),
  heroProductCount: document.getElementById("heroProductCount"),
  heroFeaturedName: document.getElementById("heroFeaturedName"),
  heroFeaturedDescription: document.getElementById("heroFeaturedDescription"),
  categoryFilters: document.getElementById("categoryFilters"),
  productGrid: document.getElementById("productGrid"),
  searchInput: document.getElementById("searchInput"),
  categoryForm: document.getElementById("categoryForm"),
  productForm: document.getElementById("productForm"),
  categoryName: document.getElementById("categoryName"),
  categoryDescription: document.getElementById("categoryDescription"),
  productName: document.getElementById("productName"),
  productCategory: document.getElementById("productCategory"),
  productPrice: document.getElementById("productPrice"),
  productSalePrice: document.getElementById("productSalePrice"),
  productStock: document.getElementById("productStock"),
  productImage: document.getElementById("productImage"),
  productDescription: document.getElementById("productDescription"),
  categoryTotalAdmin: document.getElementById("categoryTotalAdmin"),
  productTotalAdmin: document.getElementById("productTotalAdmin"),
  adminCategoryList: document.getElementById("adminCategoryList"),
  adminProductList: document.getElementById("adminProductList"),
  detailImage: document.getElementById("detailImage"),
  detailCategory: document.getElementById("detailCategory"),
  detailName: document.getElementById("detailName"),
  detailDescription: document.getElementById("detailDescription"),
  detailOriginalPrice: document.getElementById("detailOriginalPrice"),
  detailCurrentPrice: document.getElementById("detailCurrentPrice"),
  detailStock: document.getElementById("detailStock"),
  detailStatus: document.getElementById("detailStatus"),
  detailAddCart: document.getElementById("detailAddCart"),
  detailWhatsApp: document.getElementById("detailWhatsApp"),
  detailAdminShortcut: document.getElementById("detailAdminShortcut"),
  cartItems: document.getElementById("cartItems"),
  cartStatus: document.getElementById("cartStatus"),
  cartTotal: document.getElementById("cartTotal"),
  cartAddress: document.getElementById("cartAddress"),
  cartCheckout: document.getElementById("cartCheckout"),
  cartRefresh: document.getElementById("cartRefresh"),
  productBreadcrumb: document.getElementById("productBreadcrumb"),
  backToStore: document.getElementById("backToStore"),
  productTemplate: document.getElementById("productCardTemplate")
};

function saveLocalStore() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({
    categories: state.categories,
    products: state.products
  }));
}

function loadLocalStore() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return { categories: [], products: [] };
    const parsed = JSON.parse(raw);
    return {
      categories: Array.isArray(parsed.categories) ? parsed.categories : [],
      products: Array.isArray(parsed.products) ? parsed.products : []
    };
  } catch (error) {
    return { categories: [], products: [] };
  }
}

async function fetchJson(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      Accept: "application/json",
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...(options.headers || {})
    }
  });
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Falha ${response.status}`);
  }
  if (response.status === 204) return null;
  return response.json();
}

function normalizeCategory(category) {
  return {
    id: Number(category.id ?? category.id_categoria ?? category.idCategoria),
    nome: category.nome ?? "Categoria",
    descricao: category.descricao ?? ""
  };
}

function escapeSvgText(value) {
  return String(value ?? "").replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

function buildPlaceholderImage(title, category) {
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 900 720">
      <rect width="900" height="720" fill="#f6ece0"/>
      <rect x="42" y="42" width="816" height="636" rx="38" fill="#fff8ef" stroke="#e7d8c6" stroke-width="4"/>
      <circle cx="150" cy="158" r="78" fill="#efd8bc"/>
      <circle cx="730" cy="540" r="98" fill="#e3eddc"/>
      <text x="88" y="132" font-family="Arial, sans-serif" font-size="32" font-weight="700" fill="#836956">${escapeSvgText(category)}</text>
      <text x="88" y="338" font-family="Arial, sans-serif" font-size="60" font-weight="700" fill="#3a2e24">${escapeSvgText(title)}</text>
      <text x="88" y="410" font-family="Arial, sans-serif" font-size="28" fill="#7c6c5f">PetShop MRM</text>
    </svg>
  `;
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`;
}

function normalizeProduct(product) {
  const category = product.categoria ? normalizeCategory(product.categoria) : state.categories.find((item) => item.id === Number(product.categoriaId)) || null;
  const preco = Number(product.preco ?? 0);
  const precoDesconto = Number(product.precoDesconto ?? preco);
  return {
    id: Number(product.id),
    nome: product.nome ?? "Produto",
    descricao: product.descricao ?? "Produto sem descricao cadastrada.",
    preco,
    precoDesconto,
    qtdEstoque: Number(product.qtdEstoque ?? 0),
    ativo: product.ativo ?? true,
    imagem: product.imagem && product.imagem.trim() ? product.imagem : buildPlaceholderImage(product.nome ?? "Produto", category?.nome ?? "Petshop"),
    categoria: category
  };
}

function formatCurrency(value) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(Number(value || 0));
}

function getFilteredProducts() {
  const search = state.search.trim().toLowerCase();
  return state.products.filter((product) => {
    const matchesCategory = state.selectedCategory === "all" || product.categoria?.id === state.selectedCategory;
    const matchesSearch = !search || product.nome.toLowerCase().includes(search) || product.descricao.toLowerCase().includes(search) || (product.categoria?.nome ?? "").toLowerCase().includes(search);
    return matchesCategory && matchesSearch;
  });
}

function renderHero() {
  elements.heroCategoryCount.textContent = String(state.categories.length);
  elements.heroProductCount.textContent = String(state.products.length);
  if (!state.products.length) {
    elements.heroFeaturedName.textContent = "Nenhum produto cadastrado";
    elements.heroFeaturedDescription.textContent = "Use a area admin para criar sua primeira categoria e seu primeiro produto.";
    return;
  }
  const featured = [...state.products].sort((a, b) => (b.preco - b.precoDesconto) - (a.preco - a.precoDesconto))[0];
  elements.heroFeaturedName.textContent = featured.nome;
  elements.heroFeaturedDescription.textContent = featured.descricao;
}

function renderCategoryFilters() {
  const categoryItems = [{ id: "all", nome: "Todos" }, ...state.categories];
  elements.categoryFilters.innerHTML = "";
  categoryItems.forEach((category) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = `category-chip${state.selectedCategory === category.id ? " active" : ""}`;
    button.textContent = category.nome;
    button.addEventListener("click", () => {
      state.selectedCategory = category.id;
      renderCategoryFilters();
      renderProducts();
    });
    elements.categoryFilters.appendChild(button);
  });
}

function renderProducts() {
  const filteredProducts = getFilteredProducts();
  elements.productGrid.innerHTML = "";
  if (!filteredProducts.length) {
    const empty = document.createElement("div");
    empty.className = "empty-state";
    empty.textContent = state.products.length ? "Nenhum produto combina com esse filtro." : "Sua loja esta vazia por enquanto. Abra a aba Admin e crie a primeira categoria ou produto.";
    elements.productGrid.appendChild(empty);
    return;
  }
  filteredProducts.forEach((product) => {
    const card = elements.productTemplate.content.firstElementChild.cloneNode(true);
    card.querySelector("img").src = product.imagem;
    card.querySelector("img").alt = product.nome;
    card.querySelector(".product-category").textContent = product.categoria?.nome ?? "Sem categoria";
    card.querySelector("h3").textContent = product.nome;
    card.querySelector(".product-card-description").textContent = product.descricao;
    card.querySelector(".product-current-price").textContent = formatCurrency(product.precoDesconto);
    card.querySelector(".product-original-price").textContent = product.precoDesconto < product.preco ? formatCurrency(product.preco) : "";
    card.querySelector(".product-card-link").addEventListener("click", () => openProductDetail(product.id));
    elements.productGrid.appendChild(card);
  });
}

function renderAdminLists() {
  elements.categoryTotalAdmin.textContent = `${state.categories.length} cadastradas`;
  elements.productTotalAdmin.textContent = `${state.products.length} cadastrados`;
  elements.adminCategoryList.innerHTML = "";
  elements.adminProductList.innerHTML = "";

  if (!state.categories.length) {
    const empty = document.createElement("div");
    empty.className = "empty-state";
    empty.textContent = "Nenhuma categoria criada ainda.";
    elements.adminCategoryList.appendChild(empty);
  } else {
    state.categories.forEach((category) => {
      const item = document.createElement("article");
      item.className = "stack-item";
      item.innerHTML = `<div><strong>${category.nome}</strong><p>${category.descricao || "Sem descricao."}</p></div><button class="delete-button" type="button">Excluir</button>`;
      item.querySelector("button").addEventListener("click", () => deleteCategory(category.id));
      elements.adminCategoryList.appendChild(item);
    });
  }

  if (!state.products.length) {
    const empty = document.createElement("div");
    empty.className = "empty-state";
    empty.textContent = "Nenhum produto criado ainda.";
    elements.adminProductList.appendChild(empty);
  } else {
    state.products.forEach((product) => {
      const item = document.createElement("article");
      item.className = "stack-item";
      item.innerHTML = `<div><h4>${product.nome}</h4><p>${product.categoria?.nome ?? "Sem categoria"} • ${formatCurrency(product.precoDesconto)} • Estoque ${product.qtdEstoque}</p></div><button class="delete-button" type="button">Excluir</button>`;
      item.querySelector("button").addEventListener("click", () => deleteProduct(product.id));
      elements.adminProductList.appendChild(item);
    });
  }
}

function renderCategorySelect() {
  elements.productCategory.innerHTML = "";
  if (!state.categories.length) {
    const option = document.createElement("option");
    option.value = "";
    option.textContent = "Crie uma categoria primeiro";
    elements.productCategory.appendChild(option);
    return;
  }
  state.categories.forEach((category) => {
    const option = document.createElement("option");
    option.value = String(category.id);
    option.textContent = category.nome;
    elements.productCategory.appendChild(option);
  });
}

function updateConnectionBadge() {
  const labels = { api: "API conectada", local: "Modo local", loading: "Conectando..." };
  elements.connectionBadge.textContent = labels[state.mode] ?? "Conectando...";
}

function renderAll() {
  renderHero();
  renderCategoryFilters();
  renderProducts();
  renderCategorySelect();
  renderAdminLists();
  renderCart();
  updateConnectionBadge();
  if (state.currentProductId != null) renderProductDetail(state.currentProductId);
}

function setActiveView(viewName) {
  elements.views.forEach((view) => view.classList.toggle("hidden", view.dataset.view !== viewName));
  elements.navButtons.forEach((button) => {
    if (!button.dataset.viewTarget) return;
    button.classList.toggle("active", button.dataset.viewTarget === viewName);
  });
}

function openStore() {
  state.currentProductId = null;
  setActiveView("store");
  window.location.hash = "#/loja";
}

function openAdmin() {
  setActiveView("admin");
  window.location.hash = "#/admin";
}

async function openCart() {
  setActiveView("cart");
  window.location.hash = "#/carrinho";
  await loadCart();
}

function openProductDetail(productId) {
  state.currentProductId = productId;
  renderProductDetail(productId);
  setActiveView("product");
  window.location.hash = `#/produto/${productId}`;
}

function renderProductDetail(productId) {
  const product = state.products.find((item) => item.id === productId);
  if (!product) {
    openStore();
    return;
  }
  elements.detailImage.src = product.imagem;
  elements.detailImage.alt = product.nome;
  elements.detailCategory.textContent = product.categoria?.nome ?? "Sem categoria";
  elements.detailName.textContent = product.nome;
  elements.detailDescription.textContent = product.descricao;
  elements.detailCurrentPrice.textContent = formatCurrency(product.precoDesconto);
  elements.detailOriginalPrice.textContent = product.precoDesconto < product.preco ? formatCurrency(product.preco) : "";
  elements.detailStock.textContent = `${product.qtdEstoque} unidades`;
  elements.detailStatus.textContent = product.qtdEstoque > 0 ? "Disponivel" : "Sem estoque";
  elements.productBreadcrumb.textContent = `${product.categoria?.nome ?? "Produtos"} / ${product.nome}`;
  elements.detailAddCart.disabled = product.qtdEstoque <= 0;
  elements.detailAddCart.onclick = () => addProductToCart(product.id);
  elements.detailWhatsApp.onclick = () => {
    const message = encodeURIComponent(`Oi! Tenho interesse no produto ${product.nome} por ${formatCurrency(product.precoDesconto)}.`);
    window.open(`https://wa.me/5511999999999?text=${message}`, "_blank", "noopener");
  };
  elements.detailAdminShortcut.onclick = () => openAdmin();
}

function renderCart() {
  if (!elements.cartItems) return;

  const cart = state.cart;
  const itens = cart?.itens ?? [];
  elements.cartItems.innerHTML = "";
  elements.cartStatus.textContent = itens.length ? `${itens.length} item(ns)` : "Carrinho vazio";
  elements.cartTotal.textContent = formatCurrency(cart?.pedido?.valorTotal ?? 0);
  elements.cartCheckout.disabled = !cart?.pedido?.idPedido || !itens.length;

  if (!itens.length) {
    const empty = document.createElement("div");
    empty.className = "empty-state";
    empty.textContent = "Nenhum produto no carrinho ainda.";
    elements.cartItems.appendChild(empty);
    return;
  }

  itens.forEach((item) => {
    const subtotal = Number(item.precoUnitario || 0) * Number(item.quantidade || 0);
    const row = document.createElement("article");
    row.className = "stack-item cart-item";
    row.innerHTML = `
      <div>
        <strong>${item.nomeProduto}</strong>
        <p>${item.quantidade} x ${formatCurrency(item.precoUnitario)} = ${formatCurrency(subtotal)}</p>
      </div>
      <button class="delete-button" type="button">Remover</button>
    `;
    row.querySelector("button").addEventListener("click", () => removeCartItem(item.idItemPedido, cart.pedido.idPedido));
    elements.cartItems.appendChild(row);
  });
}

async function loadCart() {
  if (state.mode !== "api") {
    state.cart = null;
    renderCart();
    return;
  }

  try {
    state.cart = await fetchJson(`${ORDER_URL}/carrinho/${USER_ID}`);
  } catch (error) {
    state.cart = null;
  }
  renderCart();
}

async function addProductToCart(productId) {
  if (state.mode !== "api") {
    alert("Conecte a API para usar o carrinho.");
    return;
  }

  try {
    state.cart = await fetchJson(`${ORDER_URL}/carrinho/adicionar`, {
      method: "POST",
      body: JSON.stringify({
        idUsuario: USER_ID,
        idProduto: productId,
        quantidade: 1
      })
    });
    await openCart();
  } catch (error) {
    alert(`Nao foi possivel adicionar ao carrinho: ${error.message}`);
  }
}

async function removeCartItem(itemId, orderId) {
  try {
    const pedido = await fetchJson(`${ORDER_ITEM_URL}/${itemId}/pedido/${orderId}`, { method: "DELETE" });
    const itens = await fetchJson(`${ORDER_ITEM_URL}/pedido/${orderId}`);
    state.cart = { pedido, itens };
    renderCart();
  } catch (error) {
    alert(`Nao foi possivel remover o item: ${error.message}`);
  }
}

async function checkoutCart() {
  if (!state.cart?.pedido?.idPedido) return;

  try {
    await fetchJson(`${ORDER_URL}/finalizar/${state.cart.pedido.idPedido}`, {
      method: "PUT",
      body: JSON.stringify({ enderecoEntrega: elements.cartAddress.value.trim() })
    });
    elements.cartAddress.value = "";
    state.cart = null;
    await loadStoreData();
    await openCart();
    alert("Pedido finalizado com sucesso.");
  } catch (error) {
    alert(`Nao foi possivel finalizar o pedido: ${error.message}`);
  }
}

async function loadStoreData() {
  try {
    const [rawCategories, rawProducts] = await Promise.all([fetchJson(CATEGORY_URL), fetchJson(PRODUCT_URL)]);
    state.categories = rawCategories.map(normalizeCategory);
    state.products = rawProducts.map(normalizeProduct);
    state.mode = "api";
    saveLocalStore();
  } catch (error) {
    const local = loadLocalStore();
    state.categories = local.categories.map(normalizeCategory);
    state.products = local.products.map(normalizeProduct);
    state.mode = "local";
  }
  renderAll();
  syncRoute();
}

async function createCategory(category) {
  if (state.mode === "api") {
    const created = await fetchJson(CATEGORY_URL, { method: "POST", body: JSON.stringify({ nome: category.nome, descricao: category.descricao, ativo: true }) });
    state.categories.push(normalizeCategory(created));
    saveLocalStore();
    return;
  }
  const nextId = state.categories.reduce((max, item) => Math.max(max, item.id), 0) + 1;
  state.categories.push({ id: nextId, nome: category.nome, descricao: category.descricao });
  saveLocalStore();
}

async function createProduct(product) {
  const category = state.categories.find((item) => item.id === Number(product.categoryId));
  if (!category) throw new Error("Categoria invalida");
  if (state.mode === "api") {
    const created = await fetchJson(PRODUCT_URL, {
      method: "POST",
      body: JSON.stringify({
        nome: product.nome,
        descricao: product.descricao,
        preco: product.preco,
        precoDesconto: product.precoDesconto,
        qtdEstoque: product.qtdEstoque,
        imagem: product.imagem,
        ativo: true,
        categoriaId: category.id
      })
    });
    state.products.push(normalizeProduct({ ...created, categoria: category }));
    saveLocalStore();
    return;
  }
  const nextId = state.products.reduce((max, item) => Math.max(max, item.id), 0) + 1;
  state.products.push(normalizeProduct({ id: nextId, nome: product.nome, descricao: product.descricao, preco: product.preco, precoDesconto: product.precoDesconto, qtdEstoque: product.qtdEstoque, imagem: product.imagem, ativo: true, categoria: category }));
  saveLocalStore();
}

async function deleteCategory(categoryId) {
  if (state.products.some((product) => product.categoria?.id === categoryId)) {
    alert("Exclua os produtos dessa categoria primeiro.");
    return;
  }
  if (state.mode === "api") {
    try {
      await fetchJson(`${CATEGORY_URL}/${categoryId}`, { method: "DELETE" });
    } catch (error) {
      alert("Nao consegui excluir essa categoria pela API.");
      return;
    }
  }
  state.categories = state.categories.filter((category) => category.id !== categoryId);
  if (state.selectedCategory === categoryId) state.selectedCategory = "all";
  saveLocalStore();
  renderAll();
}

async function deleteProduct(productId) {
  if (state.mode === "api") {
    try {
      await fetchJson(`${PRODUCT_URL}/${productId}`, { method: "DELETE" });
    } catch (error) {
      alert("Nao consegui excluir esse produto pela API.");
      return;
    }
  }
  state.products = state.products.filter((product) => product.id !== productId);
  if (state.currentProductId === productId) state.currentProductId = null;
  saveLocalStore();
  renderAll();
  syncRoute();
}

function resetCategoryForm() {
  elements.categoryForm.reset();
}

function resetProductForm() {
  elements.productForm.reset();
  if (state.categories.length) elements.productCategory.value = String(state.categories[0].id);
}

function syncRoute() {
  const hash = window.location.hash || "#/loja";
  if (hash.startsWith("#/produto/")) {
    const id = Number(hash.split("/").pop());
    if (Number.isFinite(id)) {
      openProductDetail(id);
      return;
    }
  }
  if (hash === "#/admin") {
    openAdmin();
    return;
  }
  if (hash === "#/carrinho") {
    openCart();
    return;
  }
  setActiveView("store");
}

elements.searchInput.addEventListener("input", (event) => {
  state.search = event.target.value;
  renderProducts();
});

elements.categoryForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const nome = elements.categoryName.value.trim();
  const descricao = elements.categoryDescription.value.trim();
  if (!nome) {
    alert("Preencha o nome da categoria.");
    return;
  }
  try {
    await createCategory({ nome, descricao });
    resetCategoryForm();
    renderAll();
  } catch (error) {
    alert("Nao foi possivel criar a categoria.");
  }
});

elements.productForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!state.categories.length) {
    alert("Crie uma categoria antes de adicionar produtos.");
    return;
  }
  const nome = elements.productName.value.trim();
  const descricao = elements.productDescription.value.trim();
  const categoryId = Number(elements.productCategory.value);
  const preco = Number(elements.productPrice.value);
  const precoDesconto = Number(elements.productSalePrice.value || elements.productPrice.value);
  const qtdEstoque = Number(elements.productStock.value);
  const imagem = elements.productImage.value.trim();
  if (!nome || !descricao || !Number.isFinite(preco) || !Number.isFinite(precoDesconto) || !Number.isFinite(qtdEstoque)) {
    alert("Preencha os campos do produto corretamente.");
    return;
  }
  try {
    await createProduct({ nome, descricao, categoryId, preco, precoDesconto, qtdEstoque, imagem });
    resetProductForm();
    renderAll();
  } catch (error) {
    alert(`Nao foi possivel criar o produto: ${error.message}`);
  }
});

elements.backToStore.addEventListener("click", () => openStore());
elements.cartRefresh.addEventListener("click", () => loadCart());
elements.cartCheckout.addEventListener("click", () => checkoutCart());
window.addEventListener("hashchange", syncRoute);
elements.navButtons.forEach((button) => {
  if (!button.dataset.viewTarget) return;
  button.addEventListener("click", () => {
    if (button.dataset.viewTarget === "admin") {
      openAdmin();
      return;
    }
    if (button.dataset.viewTarget === "cart") {
      openCart();
      return;
    }
    openStore();
  });
});

loadStoreData();
