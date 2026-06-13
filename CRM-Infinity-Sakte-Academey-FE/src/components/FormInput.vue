<template>
  <div class="space-y-2">
    <div class="flex items-center justify-between gap-3">
      <label :for="id" class="text-[10px] font-semibold tracking-[0.22em] text-[#334155] uppercase">
        {{ label }}
      </label>
      <slot name="aside" />
    </div>

    <div
      class="group flex h-[42px] items-center rounded-[4px] border bg-white px-3 transition"
      :class="error ? 'border-red-300 ring-2 ring-red-100' : 'border-slate-200 focus-within:border-slate-300 focus-within:ring-2 focus-within:ring-slate-200/80'"
    >
      <component :is="icon" class="mr-2 h-[16px] w-[16px] shrink-0 text-slate-400" />
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        class="h-full w-full border-0 bg-transparent text-sm text-[#0f172a] outline-none placeholder:text-slate-400"
        @input="$emit('update:modelValue', $event.target.value)"
      />
      <slot name="suffix" />
    </div>

    <p v-if="error" class="text-xs text-red-600">{{ error }}</p>
  </div>
</template>

<script setup>
defineProps({
  id: {
    type: String,
    required: true
  },
  label: {
    type: String,
    required: true
  },
  modelValue: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  placeholder: {
    type: String,
    default: ''
  },
  icon: {
    type: [Object, Function],
    required: true
  },
  error: {
    type: String,
    default: ''
  }
})

defineEmits(['update:modelValue'])
</script>
