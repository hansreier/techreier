document.addEventListener('DOMContentLoaded', function () {
    const checks = document.querySelectorAll('.postLock');
    const buttons = document.querySelectorAll('.deleteButton');

    const updateAll = (isChecked) => {
        checks.forEach(c => c.checked = isChecked);
        buttons.forEach(b => b.disabled = isChecked);
    };

    checks.forEach(check => {
        if (check.checked) updateAll(true);

        check.addEventListener('change', (e) => {
            updateAll(e.target.checked);
        });
    });
});