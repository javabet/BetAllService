import * as crypto from 'crypto'


export function Md5( data:string ):string
{

    let hash = crypto.createHash('md5');

    return hash.update(data).digest('hex');
}