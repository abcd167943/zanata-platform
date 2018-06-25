// @ts-ignore
import React from 'react'
import PropTypes from 'prop-types'
import { isEmpty } from 'lodash'
import Alert from 'antd/lib/alert'
import 'antd/lib/alert/style/css'
import Button from 'antd/lib/button'
import 'antd/lib/button/style/css'
import Card from 'antd/lib/card'
import 'antd/lib/card/style/css'
import Tag from 'antd/lib/tag'
import 'antd/lib/tag/style/css'
import Modal from 'antd/lib/modal'
import 'antd/lib/modal/style/index.less'
import DateAndTimeDisplay from '../DateAndTimeDisplay'
import Textarea from 'react-textarea-autosize'

export enum resolution {
  latest = 'latest',
  original = 'original',
}

interface Latest {
  content: string
  id: number
  lastModifiedBy?: string
  lastModifiedDate: string
  plural:  boolean
  revision:  number
  status: string
}

interface Original {
  localeId: string
  modifiedTime: string
  reviewer: boolean
  revisionComment?: string
  status: string
  translations: string[]
  translator: boolean
}

interface ConcurrentModalProps {
  closeConcurrentModal: () => void
  saveResolveConflict: (latest: any, original: any, resolution: resolution) => void
  show: boolean
  conflictData?: {
    response: Latest,
    saveInfo: Original
  }
}

class ConcurrentModal extends React.Component<ConcurrentModalProps, {}> {
  public static propTypes = {
    closeConcurrentModal: PropTypes.func,
    saveResolveConflict: PropTypes.func.isRequired,
    conflictData: PropTypes.any,
    show: PropTypes.bool.isRequired,
  }
  public render () {
    const {
      closeConcurrentModal, saveResolveConflict, show, conflictData
    } = this.props
    if (!conflictData) {
      return null
    }
    const original = conflictData.saveInfo
    const latest = conflictData.response
    const lastModifiedByUsername = isEmpty(latest.lastModifiedBy)
      ? 'Someone' : latest.lastModifiedBy
    const lastModifiedDate = new Date(latest.lastModifiedDate)
    const onCancel = () => closeConcurrentModal()
    const saveLatest = () => {
      saveResolveConflict(latest, original, resolution.latest)
    }
    const saveOriginal = () => {
      saveResolveConflict(latest, original, resolution.original)
    }
    return (
      /* eslint-disable max-len */
      <Modal
        title={'Current conflicts'}
        visible={show}
        width={'46rem'}
        onCancel={onCancel}
        footer={null}>
          <Alert message={`${lastModifiedByUsername} has saved a new version while you
              were editing. Please resolve conflicts.`} type='error' />
          <Card>
            <p className='u-sizeHeight-1_1-2'>
              <strong>{lastModifiedByUsername}</strong> created a <span
                className='u-textSuccess'>Translated</span> revision <Tag
                  color='blue'>latest</Tag>
            </p>
            <span className='revisionBox'>
              <Textarea
                className='form-control'
                value={latest.content}/>
            </span>
            <span className='u-floatLeft'>
              <DateAndTimeDisplay dateTime={lastModifiedDate}
                className='u-block small u-sMT-1-2 u-sPB-1-4 u-textMuted u-textSecondary' />
            </span>
            <span className='u-floatRight'>
              <Button
                onClick={saveLatest}
                className='EditorButton Button--secondary u-rounded'>Use latest
              </Button>
            </span>
          </Card>
          <Card>
            <p className='u-sizeHeight-1_1-2'><strong>You</strong> created
              an <span className='u-textHighlight'>Unsaved</span> revision.
            </p>
            <span className='revisionBox'>
              <Textarea
                className='form-control'
                value={original.translations[0]} />
            </span>
            <span className='u-sizeHeight-1_1-2'>
              <span className='u-floatLeft'>
                <DateAndTimeDisplay dateTime={original.modifiedTime}
                  className='u-block small u-sMT-1-2 u-sPB-1-4 u-textMuted u-textSecondary' />
              </span>
              <span className='u-floatRight'>
                <Button
                  onClick={saveOriginal}
                  className='EditorButton Button--primary u-rounded'>
                  Use original
                </Button>
              </span>
            </span>
          </Card>
      </Modal>
      /* eslint-enable max-len */
    )
  }
}

export default ConcurrentModal
