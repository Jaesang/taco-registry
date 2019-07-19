export namespace Image {

	export class Layer {
    public ancestors: string;
    public command: Array<Object>;
    public comment: string;
    public created: string;
    public history: Array<Layer>;
    public id: string;
    public size: number;
    public sort_index: number;
    public uploading: boolean;

    public commandName: string;
    public commandText: string;
	}
}
