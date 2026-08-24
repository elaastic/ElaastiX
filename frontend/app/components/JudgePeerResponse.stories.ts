import type { Meta, StoryObj } from '@nuxtjs/storybook'
import JudgePeerResponse from './JudgePeerResponse.vue'
import {
	error,
	learnerExplanation,
	questionMarkdown,
} from '~/lib/storiesProvider'

const meta = {
	title: 'JudgePeerResponse',
	component: JudgePeerResponse,
	tags: ['autodocs'],
} satisfies Meta<typeof JudgePeerResponse>

export default meta
type Story = StoryObj<typeof meta>

export const Judge: Story = {
	args: {
		isLoading: false,
		error: undefined,
		peerChoice: 2,
		question: questionMarkdown,
		peerExplanation: learnerExplanation,
	},
}

export const Loading: Story = {
	args: {
		isLoading: true,
		error: undefined,
		peerChoice: 2,
		question: questionMarkdown,
		peerExplanation: learnerExplanation,
	},
}

export const Error: Story = {
	args: {
		isLoading: false,
		error: error,
		peerChoice: 2,
		question: questionMarkdown,
		peerExplanation: learnerExplanation,
	},
}
