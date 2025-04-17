document.addEventListener('DOMContentLoaded', function () {
    const deleteButton = document.getElementById('deleteButton');
    const postLockCheckbox = document.getElementById('postLock');

    function toggleDeleteButton() {
        if (postLockCheckbox && deleteButton) {
         //   deleteButton.disabled = !!postLockCheckbox.checked;
        }
    }

    toggleDeleteButton();

    if (postLockCheckbox) {
        postLockCheckbox.addEventListener('change', toggleDeleteButton);
    }
});