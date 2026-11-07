import { TipoVinculacion } from '../enums/tipoViculacion.enum';

/**
 * Horas de clase por semana permitidas según el tipo de vinculación.
 *
 * - Dedicación (máx.): Estatuto Docente U. El Bosque, art. 10 → tiempo completo 40 h, medio tiempo 20 h.
 *   Las categorías "tres cuartos" (30 h) y "cuarto de tiempo" (10 h) NO aparecen en el estatuto 2024;
 *   se mantienen proporcionales porque el sistema ya las usaba.
 * - Mínimo: el estatuto NO fija un mínimo de horas de clase. Los valores de abajo (≈40 % de la
 *   dedicación) son un valor de partida: cámbielos aquí si la Universidad define otra cifra.
 *
 * Es el único lugar donde viven estas cifras (lo usan registro-profesor y asignacion-horarios).
 */
export interface ReglaContrato {
  minHoras: number;
  maxHoras: number;
}

export const REGLAS_CONTRATO: Record<TipoVinculacion, ReglaContrato> = {
  [TipoVinculacion.TIEMPO_COMPLETO]: { minHoras: 16, maxHoras: 40 },
  [TipoVinculacion.TRES_CUARTOS_DE_TIEMPO]: { minHoras: 12, maxHoras: 30 },
  [TipoVinculacion.MEDIO_TIEMPO]: { minHoras: 8, maxHoras: 20 },
  [TipoVinculacion.CUARTO_DE_TIEMPO]: { minHoras: 4, maxHoras: 10 },
};

export function reglaDe(tipo: TipoVinculacion | string | undefined | null): ReglaContrato {
  return REGLAS_CONTRATO[tipo as TipoVinculacion] ?? { minHoras: 0, maxHoras: 0 };
}
