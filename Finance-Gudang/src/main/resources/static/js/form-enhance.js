document.addEventListener('DOMContentLoaded', function () {
  // Prevent Enter from submitting and move to next field
  const forms = document.querySelectorAll('form[method="post"]');
  forms.forEach(function (form) {
    form.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') {
        const target = e.target;

        // If input is linked to a datalist, auto-complete to first matching option
        if (target && target.tagName === 'INPUT' && target.getAttribute('list')) {
          const listId = target.getAttribute('list');
          const dl = document.getElementById(listId);
          if (dl) {
            const val = String(target.value || '').toLowerCase();
            if (val.length > 0) {
              const options = Array.from(dl.options || []);
              const match = options.find(function (opt) {
                return String(opt.value || '').toLowerCase().startsWith(val);
              });
              if (match && match.value) {
                target.value = match.value;
              }
            }
          }
        }

        // Move focus to the next focusable field within the same form
        const focusables = Array.from(form.querySelectorAll('input, select, textarea, button'))
          .filter(function (el) {
            const type = (el.getAttribute('type') || '').toLowerCase();
            const isHidden = type === 'hidden';
            const isDisabled = el.disabled;
            const isInvisible = el.offsetParent === null; // basic visibility check
            return !isHidden && !isDisabled && !isInvisible;
          });
        const currentIndex = focusables.indexOf(target);
        if (currentIndex > -1) {
          for (let i = currentIndex + 1; i < focusables.length; i++) {
            const next = focusables[i];
            if (next && typeof next.focus === 'function') {
              next.focus();
              break;
            }
          }
        }

        e.preventDefault();
      }
    });
  });

  // Improve datalist UX a bit by disabling browser autocomplete
  document.querySelectorAll('input[list]').forEach(function (inp) {
    inp.setAttribute('autocomplete', 'off');
  });
});


