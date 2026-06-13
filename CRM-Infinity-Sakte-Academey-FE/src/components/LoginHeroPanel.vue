<template>
  <aside class="relative isolate overflow-hidden bg-[#071827] text-white lg:h-[100dvh]">
    <div class="hidden h-full lg:block">
      <div class="absolute inset-0">
        <img
          class="h-full w-full object-cover object-center"
          src="../assets/bg3.jpg"
          alt="Inline skater background"
        />
        <div class="absolute inset-0 bg-[linear-gradient(180deg,rgba(7,24,39,0.64),rgba(7,24,39,0.84))]" />
        <div class="absolute inset-0 bg-[radial-gradient(circle_at_center,rgba(28,76,122,0.10),rgba(4,14,24,0.36))]" />
        <div class="absolute inset-0 bg-dots bg-[size:20px_20px] opacity-28" />
      </div>

      <div class="relative flex h-[100dvh] items-center justify-center px-10 py-12">
        <div class="flex max-w-[360px] flex-col items-center text-center">
          <img src="../assets/logo.png" alt="Infinity logo" class="h-[84px] w-auto object-contain opacity-95" />
          <h1 class="mt-5 text-[2.12rem] font-extrabold tracking-[-0.04em] text-white">Infinity</h1>
          <p class="mt-2 text-[0.9rem] font-semibold uppercase tracking-[0.26em] text-slate-300">
            Inline Skate Academy
          </p>
          <div class="mt-8 h-[4px] w-[60px] rounded-full bg-[#c25a0a]" />
          <p class="mt-8 text-[0.92rem] italic text-slate-300/80">"Ketepatan Data, Performa di Atas Roda."</p>
        </div>
      </div>
    </div>

    <div class="flex items-center justify-center bg-white px-6 pb-5 pt-7 text-[#0f172a] lg:hidden">
      <div class="flex flex-col items-center text-center">
        <img src="../assets/logo.png" alt="Infinity logo" class="h-11 w-auto object-contain" />
        <div class="mt-2 text-[1.15rem] font-extrabold tracking-[-0.04em]">Infinity</div>
        <div class="mt-1 text-[0.72rem] font-semibold uppercase tracking-[0.24em] text-slate-500">
          Inline Skate Academy
        </div>
      </div>
    </div>
  </aside>
</template>
