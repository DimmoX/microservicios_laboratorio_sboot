import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'ejemplo' })
export class EjemploPipe implements PipeTransform {
  transform(value: string): string {
    return value.toUpperCase();
  }
}
