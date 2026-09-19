(function () {
    "use strict";

    function request(url, options) {
        options = options || {};
        options.credentials = "same-origin";
        options.headers = options.headers || {};
        options.headers["X-Requested-With"] = "XMLHttpRequest";
        return fetch(url, options);
    }

    function loadCurrentUser() {
        request("/api/user/current")
            .then(function (response) {
                if (response.status === 401) {
                    window.location.href = "/login";
                    return null;
                }
                return response.json();
            })
            .then(function (result) {
                if (!result || result.code !== 200) {
                    return;
                }
                document.querySelectorAll("[data-current-user-name]").forEach(function (element) {
                    element.textContent = result.data.name || "未命名用户";
                });
                document.querySelectorAll("[data-current-user-role]").forEach(function (element) {
                    element.textContent = result.data.roleEnum || "";
                });
                applyDoctorMenuVisibility(result.data);
                applyAdminMenuVisibility(result.data);
            })
            .catch(function () {
                // 页面已有的业务请求会负责显示具体错误，这里不弹重复提示。
            });
    }

    function applyDoctorMenuVisibility(user) {
        if (!user || user.type !== 10) {
            return;
        }
        var forbidden = ["/physic", "/purchase", "/selling-price", "/report", "/user", "/tenants"];
        document.querySelectorAll(".sidebar nav a, .quick-link").forEach(function (link) {
            var href = link.getAttribute("href") || "";
            if (forbidden.some(function (path) { return href === path || href.indexOf(path + "/") === 0; })) {
                link.style.display = "none";
            }
        });
    }

    function applyAdminMenuVisibility(user) {
        var nav = document.querySelector(".sidebar nav");
        if (!nav) {
            return;
        }
        var existingAuditLink = nav.querySelector('a[href="/audit"]');
        var role = user && Number(user.role);
        if (role === 1 || role === 10) {
            if (!existingAuditLink) {
                var link = document.createElement("a");
                link.href = "/audit";
                link.textContent = "🧾 审计日志";
                nav.appendChild(link);
            }
            return;
        }
        if (existingAuditLink) {
            existingAuditLink.style.display = "none";
        }
    }

    function loadCurrentTenant() {
        if (!document.querySelector("[data-current-tenant-name]")) {
            return;
        }
        request("/api/tenants/current")
            .then(function (response) {
                return response.json();
            })
            .then(function (result) {
                if (!result || result.code !== 200 || !result.data) {
                    return;
                }
                document.querySelectorAll("[data-current-tenant-name]").forEach(function (element) {
                    element.textContent = result.data.name || "诊所";
                });
            })
            .catch(function () {
                // 租户名称加载失败时保留页面默认文案。
            });
    }

    function bindLogout() {
        document.addEventListener("click", function (event) {
            var target = event.target.closest("[data-logout]");
            if (!target) {
                return;
            }
            event.preventDefault();
            request("/api/logout", { method: "POST" }).finally(function () {
                window.location.href = "/login";
            });
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        loadCurrentUser();
        loadCurrentTenant();
        bindLogout();
    });
})();
