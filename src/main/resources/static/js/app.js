document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-reveal]').forEach((el, i) => {
    el.style.animationDelay = `${i * 60}ms`;
  });

  document.querySelectorAll('form').forEach((form) => {
    form.addEventListener('submit', () => {
      const btn = form.querySelector('button[type="submit"]');
      if (!btn) return;
      btn.dataset.originalText = btn.textContent;
      btn.textContent = 'Working...';
      btn.disabled = true;
    });
  });
});
