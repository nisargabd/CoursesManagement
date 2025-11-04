import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'joinList',
  standalone: true
})
export class JoinListPipe implements PipeTransform {

  transform(value: string[] | null | undefined, separator: string = ', '): string {
    if (!value || value.length === 0) {
      return '-';
    }
    return value.join(separator);
  }
}
