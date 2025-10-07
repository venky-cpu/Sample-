
# Angular Material Complete DOM Map

| Component | Outer Tag | Inner HTML Structure | Notes |
|-----------|-----------|--------------------|-------|
| mat-radio-button | `<mat-radio-button>` | `<input type="radio" class="mat-radio-input"> <label class="mat-radio-label"><span class="mat-radio-label-content">` | Radio button inside mat-radio-group |
| mat-radio-group | `<mat-radio-group>` | Contains multiple `<mat-radio-button>` | Group container for radios |
| mat-checkbox | `<mat-checkbox>` | `<input type="checkbox" class="mat-checkbox-input"> <label class="mat-checkbox-label"><span class="mat-checkbox-label-content">` | Standard checkbox |
| mat-slide-toggle | `<mat-slide-toggle>` | `<input type="checkbox" class="mat-slide-toggle-input"> <label class="mat-slide-toggle-label"><div class="mat-slide-toggle-thumb-container">` | Toggle switch control |
| mat-form-field | `<mat-form-field>` | `<input matInput>` or `<textarea matInput>` | Text input wrapper |
| mat-input | `<input matInput>` | Native input | Standalone input directive inside mat-form-field |
| mat-select | `<mat-select>` | `<div class="mat-select-trigger"><span class="mat-select-value">` | Trigger; overlay contains `<mat-option>` |
| mat-option | `<mat-option>` | `<span class="mat-option-text">` | Used inside select, menu; role="option" applied |
| mat-autocomplete | `<mat-autocomplete>` | `<mat-option>` | Autocomplete dropdown |
| mat-button | `<button mat-button>` / `<a mat-button>` | Inner text or `<mat-icon>` | Base button |
| mat-raised-button | `<button mat-raised-button>` | Text or `<mat-icon>` | Raised variant button |
| mat-flat-button | `<button mat-flat-button>` | Text or `<mat-icon>` | Flat variant button |
| mat-stroked-button | `<button mat-stroked-button>` | Text or `<mat-icon>` | Stroked variant button |
| mat-icon-button | `<button mat-icon-button>` | `<mat-icon>` | Icon-only button |
| mat-fab | `<button mat-fab>` | `<mat-icon>` | Floating Action Button |
| mat-mini-fab | `<button mat-mini-fab>` | `<mat-icon>` | Mini FAB |
| mat-icon | `<mat-icon>` | Text (icon name) | Can be standalone or inside button |
| mat-toolbar | `<mat-toolbar>` | `<span>` / `<div>` | Toolbar container |
| mat-sidenav | `<mat-sidenav>` | `<ng-content>` | Sidenav panel |
| mat-sidenav-container | `<mat-sidenav-container>` | `<mat-sidenav>` + `<mat-sidenav-content>` | Parent container for side navigation |
| mat-sidenav-content | `<mat-sidenav-content>` | `<ng-content>` | Main content area |
| mat-card | `<mat-card>` | `<mat-card-title>` `<mat-card-subtitle>` `<mat-card-content>` `<mat-card-actions>` | Card container |
| mat-list | `<mat-list>` | `<mat-list-item>` | List wrapper |
| mat-list-item | `<mat-list-item>` | `<div class="mat-list-item-content">` | Single list item |
| mat-menu | `<mat-menu>` | `<button mat-menu-item>` / `<mat-menu-item>` | Overlay menu; triggers elsewhere |
| mat-menu-item | `<button mat-menu-item>` / `<mat-menu-item>` | Text or `<mat-icon>` | Menu option |
| mat-dialog | `<mat-dialog-container>` | `<mat-dialog-content>` `<mat-dialog-actions>` | Overlay dialog |
| mat-tooltip | `<div mat-tooltip>` | Text | Overlay tooltip |
| mat-progress-bar | `<mat-progress-bar>` | `<div class="mat-progress-bar-primary">` | Linear progress bar |
| mat-progress-spinner | `<mat-progress-spinner>` | `<svg>` | Circular spinner |
| mat-spinner | `<mat-spinner>` | `<svg>` | Circular spinner (indeterminate) |
| mat-slide-toggle | `<mat-slide-toggle>` | `<input type="checkbox"> <label>` | Toggle switch |
| mat-tab-group | `<mat-tab-group>` | `<mat-tab-header>` `<mat-tab-body>` | Tab container |
| mat-tab | `<mat-tab>` | `<ng-template matTabContent>` | Single tab content |
| mat-expansion-panel | `<mat-expansion-panel>` | `<mat-expansion-panel-header>` `<div class="mat-expansion-panel-content">` | Accordion panel |
| mat-accordion | `<mat-accordion>` | `<mat-expansion-panel>` | Accordion container |
| mat-paginator | `<mat-paginator>` | `<button mat-icon-button>` + `<span>` | Pagination controls |
| mat-sort-header | `<mat-sort-header>` | `<span class="mat-sort-header-container">` | Table column sort header |
| mat-table | `<mat-table>` | `<mat-header-row>` `<mat-row>` `<mat-cell>` | Data table container |
| mat-header-row | `<mat-header-row>` | `<mat-header-cell>` | Header row |
| mat-row | `<mat-row>` | `<mat-cell>` | Data row |
| mat-cell | `<mat-cell>` | Text or component | Table cell |
| mat-header-cell | `<mat-header-cell>` | Text or component | Table header cell |
| mat-chip | `<mat-chip>` | `<span class="mat-chip-content">` | Chip element |
| mat-chip-list | `<mat-chip-list>` | `<mat-chip>` | Chip container |
| mat-slider | `<mat-slider>` | `<input type="range">` | Slider input |
| mat-stepper | `<mat-horizontal-stepper>` / `<mat-vertical-stepper>` | `<mat-step>` | Stepper container |
| mat-step | `<mat-step>` | `<ng-template matStepLabel>` `<ng-content>` | Single step |
| cdk-overlay-container | `<div class="cdk-overlay-container">` | Overlay content: menus, dialogs, select panels, tooltips | Always scan overlays |
| mat-badge | `<span matBadge>` | Badge text | Notification badge |
| mat-divider | `<mat-divider>` | `<hr>` or `<div>` | Visual separator |

### Notes for Automation System

1. Overlay scanning: Many components render overlays outside host component, inside `.cdk-overlay-container`. Must scan this container for selects, menus, dialogs, tooltips.
2. Native elements: Input, checkbox, radio, textarea inside Angular Material components are the actual interactive elements.
3. ARIA roles: `role="option"`, `role="radio"`, `role="checkbox"` â reliable across versions.
4. Class prefixes: `.mat-*` are stable and can be used as fallback.
5. Detection order recommended: host tag â inner native element â ARIA role â class â overlay container
