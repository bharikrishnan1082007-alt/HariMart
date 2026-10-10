/**
 * HariMart Live Autocomplete Search Bar
 */

document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('headerSearchInput');
    const dropdown = document.getElementById('searchAutocompleteDropdown');
    const contextPath = searchInput ? (searchInput.dataset.contextPath || '') : '';

    if (!searchInput || !dropdown) return;

    let debounceTimer;

    searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        const query = searchInput.value.trim();

        if (query.length < 2) {
            dropdown.style.display = 'none';
            dropdown.innerHTML = '';
            return;
        }

        debounceTimer = setTimeout(async () => {
            try {
                const res = await fetch(`${contextPath}/autocomplete?q=${encodeURIComponent(query)}`);
                const suggestions = await res.json();

                if (suggestions && suggestions.length > 0) {
                    dropdown.innerHTML = suggestions.map(s => 
                        `<div class="autocomplete-item" onclick="selectSearchSuggestion('${s.replace(/'/g, "\\'")}')">${s}</div>`
                    ).join('');
                    dropdown.style.display = 'block';
                } else {
                    dropdown.style.display = 'none';
                }
            } catch (err) {
                dropdown.style.display = 'none';
            }
        }, 250);
    });

    document.addEventListener('click', (e) => {
        if (!searchInput.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = 'none';
        }
    });
});

function selectSearchSuggestion(val) {
    const input = document.getElementById('headerSearchInput');
    const form = document.getElementById('headerSearchForm');
    if (input && form) {
        input.value = val;
        form.submit();
    }
}
