document.addEventListener('DOMContentLoaded', function () {
    const checks = document.querySelectorAll('.postLock');
    const buttons = document.querySelectorAll('.deleteButton');
    const warnings = document.querySelectorAll('.deleteWarning');

    const updateAll = (isChecked) => {
        checks.forEach(c => c.checked = isChecked);
        buttons.forEach(b => b.disabled = isChecked);
        warnings.forEach(w => w.style.display = isChecked ? 'none' : 'block');
    };

    checks.forEach(check => {
        if (check.checked) updateAll(true);

        check.addEventListener('change', (e) => {
            updateAll(e.target.checked);
        });
    });
});