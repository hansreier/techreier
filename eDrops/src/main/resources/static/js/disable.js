document.addEventListener('DOMContentLoaded', function () {
    const deleteButton = document.getElementById('deleteButton');
    const postLockCheckbox = document.getElementById('postLock');

    if (postLockCheckbox && deleteButton) {
        deleteButton.disabled = postLockCheckbox.checked;
        postLockCheckbox.addEventListener('change', function () {
            deleteButton.disabled = postLockCheckbox.checked;
        });
    }
});