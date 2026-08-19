import type { ReactNode } from "react";

export default function FormField({
  label,
  children,
}: {
  label: string;
  children: ReactNode;
}) {
  return (
    <div>
      <label className="mb-1.5 block text-xs font-medium text-foreground-700">
        {label}
      </label>
      {children}
    </div>
  );
}
